<?php 

namespace api\modules\v1\utils;

class Util
{
    /**
     * 获取代币发行量
     *
     * $db 数据库连接对象
    */
	function get_max_supply($db) {
        // 获取代币发行量
        $sql = "select token, supply from token";
        $supply = $db->get_result($sql);
        $max_supply = [];
        if ($supply) {
            foreach($supply as $val) {
                $max_supply[$val["token"]] = $val["supply"];
            }
        }
        return $max_supply;
    }

    function get_calc_volume($info) {
        $value = $max_supply[$info["token"]] * $info["close"];
        $info["value_sort"] = $value;
        if ($value / 1000000 > 1000) {
            $value = round(($value / 1000000000), 3)."B";
        } else {
            $value = round(($value / 1000000), 3)."M";
        }
        $info["value"] = $value;
        return $info;
    }
}