<?php
/**
 * Created by PhpStorm.
 * User: ricky
 * Date: 19-1-18
 * Time: 下午2:05
 */


namespace api\modules\v2\models;

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
    public function get_list_for_search($search)
    {
        $sql = "select exchange exchange_name, pair, vol vol_format, icon from exchange where exchange like '{$search}%' ";
        return Yii::$app->db->createCommand($sql)->queryAll();
    }


    /**
     * 获取交易所列表
     *
     * @return array 交易所列表
     */
    public function get_list() {
        $sql = "select exchange exchange_name, pair, vol vol_format, icon from wp_exchange order by vol desc";
        return Yii::$app->db->createCommand($sql)->queryAll();
    }
}