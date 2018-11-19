<?php
namespace api\modules\v1\models;

use Yii;
use yii\db\ActiveRecord;

class HotSearch extends ActiveRecord
{
    
    /**
     * @return string Active Record 类关联的数据库表名称
     */
    public static function tableName()
    {
        return '{{%hot_search}}';
    }

    /**
     * 根据搜索词获取对应的列表
     *
     * @param $search 搜索词
     */
    public function get_list_for_search($search) {
        $sql = "select token, search_count from hot_search where token = '{$search}' ";
	    return Yii::$app->db->createCommand($sql)->queryOne();
    }
}
