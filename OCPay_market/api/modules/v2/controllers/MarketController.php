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
}