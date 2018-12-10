<?php
namespace api\modules\v2\services;

use Yii;
use api\modules\v2\models\Collect;

class Collect
{
	/**
     * 添加收藏
     * 
     * @param $exchange    交易所
     * @param $user_id     用户id
     * @param $curr_token  当前币种
     * @param $plat_type   平台类型
    */
    function add($exchange, $user_id, $curr_token, $plat_type) {
        $col = Collect::get_col($exchange, $user_id, $curr_token, $plat_type);

        $col_type = 1;
        if ($exchange) {
            $col_type = 2;
        }

        if ($col) {
            $c_type = $col["type"];
            if ($c_type == 1 || $c_type == 2) $c_type = 0;
            else $c_type = 1;

            $id = $col["ID"];
            Collect::updateAll(['type' => $c_type], 'ID = '.$id);
        } else {
            Collect::add($user_id, $curr_token, $exchange, $col_type, $plat_type);
        }
        return '';
    }
}