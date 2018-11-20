<?php

namespace api\modules\v1\controllers;

use yii;
use yii\rest\ActiveController;
use yii\web\Response;
use api\modules\v1\models\Code;
use api\modules\v1\models\Collect;
use api\modules\v1\models\Util;
use api\modules\v1\models\HotSearch;
use api\modules\v1\models\Market;
use api\modules\v1\models\Exchange;

class MarketController extends BaseController
{

    // 添加收藏
	public function actionAdd() {
		Yii::$app->response->format=Response::FORMAT_JSON;

		$request = Yii::$app->request;
		$user_id = $request->post("user_id", 0);
		$curr_token = $request->post("curr_token", ""); // 当前代币
		if ($user_id == 0 || $curr_token == "") {
			return ["code" => 1036, "msg" => Code::code(1036)];
		}

		$exchange = $request->post("exchange", "");
		$plat_type = $request->post("plat_type", 1);
		
		$col = Collect::get_col($exchange, $user_id, $curr_token, $plat_type);

		if ($col) {
			$c_type = $col["type"];
	        if ($c_type == 1 || $c_type == 2) $c_type = 0;
	        else $c_type = 1;

	        $id = $col["ID"];
	        Collect::updateAll(['type' => $c_type], 'ID = '.$id);
		} else {
			Collect::add($user_id, $curr_token, $exchange, $col_type, $plat_type);
		}

		return ['code' => 200];
	}

	// 收藏列表
	public function actionFavor() {
		Yii::$app->response->format=Response::FORMAT_JSON;

		$request = Yii::$app->request;
		$redis = Yii::$app->redis;
		$order = $request->post("order", 1);
		$user_id = $request->post("user_id", 0);
		$plat_type = $request->post("plat_type", 1);
		if ($user_id == 0) {
			return ["code" => 1011, "msg" => Code::code(1011)];
		}

		$col = Collect::get_list_for_userid($user_id, $plat_type);

		$res = [];
		if ($col) {
			$time = Market::get_create_time();
	        $redis = Yii::$app->redis;
	        $market = $redis->zrange("market", 0, -1);
	        $opt_info = []; // 最优
	        $nor_info = [];

	        if ($market){
	            $o_info = [];
	            foreach($market as $key => $val) {
	                $source = json_decode($val, true);
	                $nor_info[$source["exchange_name"].$source["token"]] = $source;
	                $o_info[] = $source;
	            }

	            // 处理最优
	            if ($o_info) {
	                $sort = array_column($o_info, "close");
	                array_multisort($sort, SORT_DESC, $o_info);

	                foreach($o_info as $key =>$val) {
	                    if (!isset($opt_info[$val["token"]])) $opt_info[$val["token"]] = $val;
	                }
	            }

	        }

	        $max_supply = Util::get_max_supply();
	        foreach($col as $val) {
	            $token = $val["token"];
	            $exchange = $val["exchange"];
	            $type = 1;
	            if ($val["col_type"] == 1) {
	                if (isset($opt_info[$token])) {
	                    $info = $opt_info[$token];
	                } else {
	                	$info = Market::get_list_for_token($create_time, $token);
	                }
	            } else {
	                if (isset($nor_info[$exchange.$token])) {
	                    $info = $nor_info[$exchange.$token];
	                } else {
	                	$info = Market::get_list_for_exchange($time, $token, $exchange);
	                }
	                $type = 2;
	            }

	            if ($info) {
	                $info["close"] = sprintf("%0.4f", $info["close"]);
	                $info["degree"] = sprintf("%0.2f", $info["degree"]);
	                $info["vol_format"] = number_format($info["vol"], 0);
	                $info["collect_status"] = 1;
	                $info["type"] = $type;

	                if (isset($max_supply[$info["token"]])) {
	                    $value = $max_supply[$info["token"]] * $info["close"];
	                    $info["value_sort"] = $value;
	                    if ($value / 1000000 > 1000) {
	                        $value = round(($value / 1000000000), 3)."B";
	                    } else {
	                        $value = round(($value / 1000000), 3)."M";
	                    }
	                    $info["value"] = $value;

	                    $res[] = $info;
	                }

	            }
	        }

	        if ($res) $res = self::get_market_sort($order, $res);
	    }
	    return ["code" => 200, "data" => $res];
	}

	// token 列表
	public function actionList() {
	    Yii::$app->response->format=Response::FORMAT_JSON;

		$request = Yii::$app->request;
		$order = $request->post("order", 1);
		$user_id = $request->post("user_id", 0);
		$search = $request->post("search", "");
		$plat_type = $request->post("plat_type", 1);

		$redis = Yii::$app->redis;
	    $res = $redis->zrange("market", 0, -1);

	    $info = [];
	    if (!$res) {
	    	$time = Market::get_create_time();

	        if ($search) {
	        	$info = Market::get_list_search_token($time, $search);
	        } else {
	        	$info = Market::get_list_for_time($time);
	        }
	    } else {
	        foreach($res as $key => $val) {
	            $source = json_decode($val, true);
	            if ($search) {
	                if (stripos($source["token"], $search) !== false) $info[] = $source;
	            } else {
	                $info[] = $source;
	            }
	        }
	    }

	    // 处理最优
	    $data = Collect::get_opt_col($user_id, $plat_type);
	    $norm_col = $data["norm_col"];
	    $opt_col = $data["opt_col"];

	    // 获取代币发行量
	    $max_supply = Util::get_max_supply();

	    $arr = [];
	    if ($info) {
	        // 处理排序
	        $res = [];
	        foreach($info as $val) {
	            if (isset($max_supply[$val["token"]])) {
	                $value = $max_supply[$val["token"]] * $val["close"];
	                $val["value_sort"] = $value;
	                if ($value / 1000000 > 1000) {
	                    $value = round(($value / 1000000000), 3)."B";
	                } else {
	                    $value = round(($value / 1000000), 3)."M";
	                }

	                $val["value"] = $value;
	            } else {
	                continue;
	            }
	            $val["close"] = sprintf("%0.4f", $val["close"]);
	            $val["degree"] = sprintf("%0.2f", $val["degree"]);
	            $val["vol"] = $val["vol"] * $val["close"];
	            $val["vol_format"] = number_format(sprintf("%0.2f", $val["vol"]));
	            //$val["vol_format"] = number_format($val["vol"], 0);
	            $val["collect_status"] = 0;
	            if (isset($norm_col[$val["token"]."_".$val["exchange_name"]])) $val["collect_status"] = 1;

	            $res[$val["token"]][] = $val;
	        }

	        // 处理最优
	        foreach($res as $key =>$val) {
	            $tem = $res[$key];
	            //unset($tem[0]);
	            if (isset($opt_col[$res[$key][0]["token"]])) $res[$key][0]["collect_status"] = 1;
	            else $res[$key][0]["collect_status"] = 0;

	            $res[$key][0]["child"] = array_values($tem);

	            $arr[] = $res[$key][0];
	            unset($res[$key]);
	        }

	        $arr = self::get_market_sort($order, $arr);
	    }

	    if ($search) {
	    	$hot_search = HotSearch::get_list_for_search($search);
	        if ($hot_search) {
	        	HotSearch::updateAll(["search_count" => $hot_search["search_count"] + 1], " token = '{$search}' ");
	        } else {
	        	$hot = new HotSearch();
	        	$hot->token = $search;
	        	$hot->search_count = 1;
	        	$hot->save();
	        }
	    }

	    return ["code" => 200, "data" => $arr];
	}

	// 交易所列表
	public function actionExchange() {
		Yii::$app->response->format=Response::FORMAT_JSON;

		$request = Yii::$app->request;
		$order = $request->post("order", 5);
		$search = $request->post("search", "");
	    if ($search) {
	    	$info = Exchange::get_list_for_search($search);
	        $sql = "select token, search_count from wp_hot_search where token = '{$search}' ";
	        $hot_search = Yii::$app->db->createCommand($sql)->queryOne(); 
	        if ($hot_search) {
	        	HotSearch::updateAll(['search_count' => ($hot_search["search_count"] + 1)], " token = '{$search}' ");
	        } else {
	        	$hot = new HotSearch();
	        	$hot->token = $search;
	        	$hot->search_count = 1;
	        	$hot->save();
	        }
	    } else {
	        $sql = "select exchange exchange_name, pair, vol vol_format, icon from wp_exchange order by vol desc";
	        $info = Yii::$app->db->createCommand($sql)->queryAll(); 
	    }

	    if ($info) {
	        if ($order == 6) {
	            $sort = array_column($info, "vol_format");
	            array_multisort($sort, SORT_ASC, $info);
	        } else if ($order == 3) {
	            $sort = array_column($info, "pair");
	            array_multisort($sort, SORT_DESC, $info);
	        } else if ($order == 4) {
	            $sort = array_column($info, "pair");
	            array_multisort($sort, SORT_ASC, $info);
	        }
	        foreach($info as $key => $val) {
	            $info[$key]["vol_format"] = "$".$val["vol_format"]."M";
	        }
	    } else {
	        $info = [];
	    }

	    return ["code" => 200, "data" => $info];
	}

	// 初始搜索
	public function actionSearchhot() {
	    Yii::$app->response->format=Response::FORMAT_JSON;

		$sql = "select token from wp_hot_search limit 5";
		$info = Yii::$app->db->createCommand($sql)->queryAll(); 
		if (!$info) $info = [];
		return ["code" => 200, "data" => $info];
	}

	function get_market_sort ($order, $arr) {
	    if ($order == 2) {
	        $sort = array_column($arr, "value_sort");
	        array_multisort($sort, SORT_ASC, $arr);
	    } else if ($order == 3) {
	        $sort = array_column($arr, "vol");
	        array_multisort($sort, SORT_DESC, $arr);
	    } else if ($order == 4) {
	        $sort = array_column($arr, "vol");
	        array_multisort($sort, SORT_ASC, $arr);
	    } else if ($order == 5) {
	        $sort = array_column($arr, "degree");
	        array_multisort($sort, SORT_DESC, $arr);
	    } else if ($order == 6) {
	        $sort = array_column($arr, "degree");
	        array_multisort($sort, SORT_ASC, $arr);
	    } else {
	        $sort = array_column($arr, "value_sort");
	        array_multisort($sort, SORT_DESC, $arr);
	    }
	    return $arr;
	}

}
