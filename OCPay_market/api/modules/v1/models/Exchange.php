<?php
namespace api\modules\v1\models;

use Yii;
use yii\db\ActiveRecord;

class Exchange extends ActiveRecord
{
    
    /**
     * @return string Active Record 类关联的数据库表名称
     */
    public static function tableName()
    {
        return '{{%exchange}}';
    }

    /**
     * 根据搜索词获取指定交易所
     *
     * @param $search 搜索词
     */
    public function get_list_for_search($search) {
    	$sql = "select exchange exchange_name, pair, vol vol_format, icon from wp_exchange where exchange like '{$search}%' ";
	    return Yii::$app->db->createCommand($sql)->queryAll(); 
    }
}