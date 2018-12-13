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

    function get_list_for_userid($user_id, $plat_type) {
        $col = Collect::get_list_for_userid($user_id, $plat_type);

        $res = [];
        if ($col) {
            $time = Market::get_create_time();
            $redis = Yii::$app->redis;
            $market = $redis->zrange("market", 0, -1);
            $opt_info = []; // 最优
            $nor_info = [];

            if ($market){
                $o_info = [];
                foreach($market as $key => $val) {
                    $source = json_decode($val, true);
                    $nor_info[$source["exchange_name"].$source["token"]] = $source;
                    $o_info[] = $source;
                }

                // 处理最优
                if ($o_info) {
                    $sort = array_column($o_info, "close");
                    array_multisort($sort, SORT_DESC, $o_info);

                    foreach($o_info as $key =>$val) {
                        if (!isset($opt_info[$val["token"]])) $opt_info[$val["token"]] = $val;
                    }
                }

            }

            $max_supply = Util::get_max_supply();
            foreach($col as $val) {
                $token = $val["token"];
                $exchange = $val["exchange"];
                $type = 1;
                if ($val["col_type"] == 1) {
                    if (isset($opt_info[$token])) {
                        $info = $opt_info[$token];
                    } else {
                        $info = Market::get_list_for_token($create_time, $token);
                    }
                } else {
                    if (isset($nor_info[$exchange.$token])) {
                        $info = $nor_info[$exchange.$token];
                    } else {
                        $info = Market::get_list_for_exchange($time, $token, $exchange);
                    }
                    $type = 2;
                }

                if ($info) {
                    $info["close"] = sprintf("%0.4f", $info["close"]);
                    $info["degree"] = sprintf("%0.2f", $info["degree"]);
                    $info["vol_format"] = number_format($info["vol"], 0);
                    $info["collect_status"] = 1;
                    $info["type"] = $type;

                    if (isset($max_supply[$info["token"]])) {
                        $value = $max_supply[$info["token"]] * $info["close"];
                        $info["value_sort"] = $value;
                        if ($value / 1000000 > 1000) {
                            $value = round(($value / 1000000000), 3)."B";
                        } else {
                            $value = round(($value / 1000000), 3)."M";
                        }
                        $info["value"] = $value;

                        $res[] = $info;
                    }

                }
            }

            if ($res) $res = Util::get_market_sort($order, $res);
        }
        return $res;
    }
}