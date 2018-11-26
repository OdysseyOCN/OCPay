<?php

namespace api\modules\v1\models;

use Yii;

// 操作日志 
class Log{

	// 记录操作日志
	public static function log($msg) {
		// $userBase = Yii::$app->session['userInfo']['userBase'];
		// $uid = $userBase['uid'];
		// $login_id = $userBase['login_id'];
		// $name = $userBase['name'];
		// 当前执行时间:{$time} $time = date("Y-m-d H:i:s", time());

		Yii::error("msg:{$msg}", 'catalog');
	}
}