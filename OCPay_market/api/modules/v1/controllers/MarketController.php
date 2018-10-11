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

}