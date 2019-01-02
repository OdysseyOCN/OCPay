<?php
namespace api\modules\v2\models;

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

    /**
     * 获取用户收藏记录
     * @param $user_id 用户id
     * @param $plat_type  
     */
    public function get_list_for_userid($user_id, $plat_type ) {
        $sql = "select token, exchange, col_type from collect where user_id = $user_id and type in (1, 2) and plat_type = $plat_type";
        return Yii::$app->db->createCommand($sql)->queryAll();
    }

    /**
     *
     * @param $user_id 用户id
     * @param $plat_type
     */
    public function get_opt_col($user_id, $plat_type) {
        $norm_col = [];
        $opt_col = [];
        if ($user_id) {
            $sql = "select token, exchange, col_type from collect where user_id = $user_id and type = 1 and plat_type = $plat_type";
            $col = Yii::$app->db->createCommand($sql)->queryAll();

            if ($col) {
                foreach($col as $val) {
                    if ($val["col_type"] == 1) $opt_col[$val["token"]] = 1;
                    else $norm_col[$val["token"]."_".$val["exchange"]] = 1;
                }
            }
        }
        $data["norm_col"] = $norm_col;
        $data["opt_col"] = $opt_col;
        return $data;
    }
}