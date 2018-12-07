<?php
namespace api\modules\v1\models;

use Yii;
use yii\db\ActiveRecord;

class Collect extends ActiveRecord
{
    
    /**
     * @return string Active Record 类关联的数据库表名称
     */
    public static function tableName()
    {
        return '{{%collect}}';
    }

    /**
     * 获取已有的收藏数据用于修改
     * 
     * @param $exchange    交易所
     * @param $user_id     用户id
     * @param $curr_token  当前币种
     * @param $plat_type   平台类型
    */
    public function get_col($exchange, $user_id, $curr_token, $plat_type) {
        // 处理最优收藏
        $sql = "select ID, type from collect where user_id = $user_id and token = '{$curr_token}' and plat_type = $plat_type ";

    	if ($exchange) { // 处理普通收藏
			$sql = "select ID, type from collect where user_id = $user_id and token = '{$curr_token}' and exchange = '{$exchange}' and plat_type = $plat_type ";
		} 
		return Yii::$app->db->createCommand($sql)->queryOne();
    }

    /**
     * 添加收藏
     * 
     * @param $exchange    交易所
     * @param $user_id     用户id
     * @param $curr_token  当前币种
     * @param $plat_type   平台类型
    */
    function add_collect($exchange, $user_id, $curr_token, $plat_type) {
        $col = self::get_col($exchange, $user_id, $curr_token, $plat_type);

        $col_type = 1;
        if ($exchange) {
            $col_type = 2;
        }

        if ($col) {
            $c_type = $col["type"];
            if ($c_type == 1 || $c_type == 2) $c_type = 0;
            else $c_type = 1;

            $id = $col["ID"];
            self::updateAll(['type' => $c_type], 'ID = '.$id);
        } else {
            self::add($user_id, $curr_token, $exchange, $col_type, $plat_type);
        }
        return '';
    }


    /**
     * 添加收藏
     *
     * @param $user_id 用户id
     * @param $curr_token 当前币种
     * @param $exchange 交易所
     * @param $col_type 
     * @param $plat_type 
     */
    public function add($user_id, $curr_token, $exchange, $col_type, $plat_type) {
        $collect = new self();
        $collect->user_id = $user_id;
        $collect->token = $curr_token;
        $collect->exchange = $exchange;
        $collect->type = 1;
        $collect->col_type = $col_type;
        $collect->plat_type = $plat_type;
        $collect->create_time = time();
        $collect->save();
    }

}