<?php
namespace api\modules\v2\filters;

use Yii;
use yii\base\ActionFilter;
use api\modules\v2\models\Log;
/**
 * 操作方法前缀过滤
 * 功能: 拦截权限
 * 作者: caopeng
 * 时间: 2018-11-29
*/
class AccessFilter extends ActionFilter {
	
	public function beforeAction($action) {
	    // 处理日志
		$met = $action->id;
		$con = $action->controller->id;
		if ($_POST) Log::log("input: $con/$met ".json_encode($_POST));

		// 操作方法 $action->id 控制器 $action->controller->id
		if (getenv("HTTP_CLIENT_IP"))
		$ip = getenv("HTTP_CLIENT_IP");
		else if(getenv("HTTP_X_FORWARDED_FOR"))
		$ip = getenv("HTTP_X_FORWARDED_FOR");
		else if(getenv("REMOTE_ADDR"))
		$ip = getenv("REMOTE_ADDR");

	    //$key = "ip_".md5($ip); 
	    // ip限制 每五分钟200次
	    $key = "ip_".$ip;
		$info = Yii::$app->redis->get($key);
		if ($info) {
			$maxTimes = 200;
			$info = json_decode($info, true);
			$times = $info["times"];
			$time = $info["time"];

		    $second = date("i", $time);
        	if (($second % 5 == 0) || (time() - $time > 300)) {
        		$times = 0;
        		$time = time();
        	}

			if ($times >= $maxTimes) {
        	    echo json_encode(["code" => 404]); exit;
            } else {
            	$data = [
	        	    "times" => $times + 1,
	        	    "time" => $time,
	        	];
	        	Yii::$app->redis->set($key, json_encode($data));
            }
        } else {
        	$data = [
        	    "times" => 0,
        	    "time" => time()
        	];
        	Yii::$app->redis->set($key, json_encode($data));
        }
        return $action;
	}
}