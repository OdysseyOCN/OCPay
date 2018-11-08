<?php

namespace api\modules\v1\models;

use Yii;
use yii\db\ActiveRecord;

class Market extends ActiveRecord
{
	/**
	 * @return string Active Record 类关联的数据库表名称
	 */
	public static function tableName()
	{
		return '{{%market}}';
	}

	/**
	 * 获取数据最近时间
	 *
	 * @return int 获取最近时间
	 */
	public function get_create_time() {
		$sql = "select create_time from market order by create_time desc limit 1";
	    $time = Yii::$app->db->createCommand($sql)->queryOne();
	    if (isset($time["create_time"])) return $time["create_time"];
	    return time();
	}

	/**
	 * 获取当时时间的token信息
	 *
	 * @param $time 当前时间
	 * @param $token
	 * @return array 返回token信息
	 */
	public function get_list_for_token($time, $token) {
	    $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and token in ('{$token}') order by `close` limit 1";
	    return Yii::$app->db->createCommand($sql)->queryOne();
	}

	/**
	 * 获取当前时间的指定交易所的token信息
	 * 
	 * @param $time 当前时间
	 * @param $token
	 * @param $exchange 指定交易所
	 * @return array 返回指定交易所的token信息
	 */
	public function get ($time, $token, $exchange) {
		$sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and token = '{$token}' and exchange_name = '{$exchange}' ";
	    return Yii::$app->db->createCommand($sql)->queryOne();
	}
}