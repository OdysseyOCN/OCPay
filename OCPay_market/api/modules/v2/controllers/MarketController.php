<?php

namespace api\modules\v1\controllers;

use yii;
use yii\rest\ActiveController;
use yii\web\Response;
use api\modules\v2\models\Code;
use api\modules\v2\Services\SCollect;
use api\modules\v2\Services\SMarket;

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

        $info = SMarket::get_list($search, $user_id, $plat_type, $order);

        return ["code" => 200, "data" => $info];
    }

    // 交易所列表
    public function actionExchange() {
        Yii::$app->response->format=Response::FORMAT_JSON;

        $request = Yii::$app->request;
        $order = $request->post("order", 5);
        $search = $request->post("search", "");
        if ($search) {
            $info = Exchange::get_list_for_search($search);
            HotSearch::add_search($search);
        } else {
            $info = Exchange::get_list();
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