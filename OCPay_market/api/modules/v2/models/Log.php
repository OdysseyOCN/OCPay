<?php

namespace api\modules\v2\models;

use Yii;

/**
 * 操作日志 
 * 功能: 拦截权限
 * 作者: caopeng
 * 时间: 2018-11-28
*/
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