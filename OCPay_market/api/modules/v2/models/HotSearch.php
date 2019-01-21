<?php
namespace api\modules\v2\models;

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

    /**
     * 添加热搜词
     *
     * @param $search 搜索词
     */
    public function add_search($search) {
        $sql = "select token, search_count from hot_search where token = '{$search}' ";
        $hot_search = Yii::$app->db->createCommand($sql)->queryOne();
        if ($hot_search) {
            HotSearch::updateAll(['search_count' => ($hot_search["search_count"] + 1)], " token = '{$search}' ");
        } else {
            $hot = new HotSearch();
            $hot->token = $search;
            $hot->search_count = 1;
            $hot->save();
        }
    }
}