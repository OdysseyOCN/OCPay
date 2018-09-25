<?php
// 工具类
class Utils {
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
}