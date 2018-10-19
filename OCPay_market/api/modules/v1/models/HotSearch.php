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
}
