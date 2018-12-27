<?php

namespace api\modules\v1\controllers;

use yii;
use yii\rest\ActiveController;
use yii\web\Response;
use api\modules\v2\models\Code;
use api\modules\v2\Services\SCollect;

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
		SCollect::add($exchange, $user_id, $curr_token, $plat_type);

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

		$res = SCollect::get_list_for_userid($user_id, $plat_type, $order);
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

            $arr = Util::get_market_sort($order, $arr);
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
}