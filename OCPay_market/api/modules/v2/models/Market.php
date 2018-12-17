<?php

namespace api\modules\v2\models;

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

}