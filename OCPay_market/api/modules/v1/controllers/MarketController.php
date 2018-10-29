<?php

namespace api\modules\v1\controllers;

use yii;
use yii\rest\ActiveController;
use yii\web\Response;
use api\modules\v1\models\Code;
use api\modules\v1\models\Collect;
use api\modules\v1\models\Util;
use api\modules\v1\models\HotSearch;
use api\modules\v1\models\Constant;

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

		if ($exchange) { // 处理普通收藏
			$sql = "select ID, type from collect where user_id = $user_id and token = '{$curr_token}' and exchange = '{$exchange}' and plat_type = $plat_type ";
            $col_type = 2;
		} else { // 处理最优收藏
			$sql = "select ID, type from collect where user_id = $user_id and token = '{$curr_token}' and plat_type = $plat_type ";
            $col_type = 1;
		}
		$col = Yii::$app->db->createCommand($sql)->queryOne(); 

		if ($col) {
			$c_type = $col["type"];
	        if ($c_type == 1 || $c_type == 2) $c_type = 0;
	        else $c_type = 1;

	        $id = $col["ID"];
	        Collect::updateAll(['type' => $c_type], 'ID = '.$id);
		} else {
			$collect = new Collect();
			$collect->user_id = $user_id;
			$collect->token = $curr_token;
			$collect->exchange = $exchange;
			$collect->type = 1;
			$collect->col_type = $col_type;
			$collect->plat_type = $plat_type;
			$collect->create_time = time();
			$collect->save();
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

		$sql = "select token, exchange, col_type from collect where user_id = $user_id and type in (1, 2) and plat_type = $plat_type";
		$col = Yii::$app->db->createCommand($sql)->queryAll();

		$res = [];
		if ($col) {
	        $sql = "select create_time from market order by create_time desc limit 1";
	        $time = Yii::$app->db->createCommand($sql)->queryOne();
	        if (isset($time["create_time"])) $time = $time["create_time"];
	        else $time = time();
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
	                    $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and token in ('{$token}') order by `close` limit 1";
	                    $info = Yii::$app->db->createCommand($sql)->queryOne();
	                }
	            } else {
	                if (isset($nor_info[$exchange.$token])) {
	                    $info = $nor_info[$exchange.$token];
	                } else {
	                    $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and token = '{$token}' and exchange_name = '{$exchange}' ";
	                    $info = Yii::$app->db->createCommand($sql)->queryOne();
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
	        $sql = "select create_time from wp_market order by create_time desc limit 1";
	        $time = Yii::$app->db->createCommand($sql)->queryOne(); 
	        if (isset($time["create_time"])) $time = $time["create_time"];
	        else $time = time();

	        if ($search) {
	            $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from wp_market where create_time = $time and (currency = 'USD' or currency = 'USDT') and token like '{$search}%' order by `vol` desc ";
	            $info = Yii::$app->db->createCommand($sql)->queryAll(); 
	        } else {
	            $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from wp_market where create_time = $time and (currency = 'USD' or currency = 'USDT') order by `vol` desc ";
	            $info = Yii::$app->db->createCommand($sql)->queryAll(); 
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
	    $norm_col = [];
	    $opt_col = [];
	    if ($user_id) {
	        $sql = "select token, exchange, col_type from wp_collect where user_id = $user_id and type = 1 and plat_type = $plat_type";
	        $col = Yii::$app->db->createCommand($sql)->queryAll();

	        if ($col) {
	            foreach($col as $val) {
	                if ($val["col_type"] == 1) $opt_col[$val["token"]] = 1;
	                else $norm_col[$val["token"]."_".$val["exchange"]] = 1;
	            }
	        }
	    }

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
	        $sql = "select token, search_count from wp_hot_search where token = '{$search}' ";
	        $hot_search = Yii::$app->db->createCommand($sql)->queryOne();
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
	        $sql = "select exchange exchange_name, pair, vol vol_format, icon from wp_exchange where exchange like '{$search}%' ";
	        $info = Yii::$app->db->createCommand($sql)->queryAll(); 

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

}
