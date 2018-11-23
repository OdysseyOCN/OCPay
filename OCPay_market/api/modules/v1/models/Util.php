<?php 

namespace api\modules\v1\models;

use yii\base\Model;

class Code extends Model
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

    /**
     * 获取排序的数据
     *
     * @param $order 排序方式
     * @param $arr 原始数据
     *
     * @return array 排序后的数组
     */
    function get_market_sort ($order, $arr) {
        if ($order == 2) {
            $sort = array_column($arr, "value_sort");
            array_multisort($sort, SORT_ASC, $arr);
        } else if ($order == 3) {
            $sort = array_column($arr, "vol");
            array_multisort($sort, SORT_DESC, $arr);
        } else if ($order == 4) {
            $sort = array_column($arr, "vol");
            array_multisort($sort, SORT_ASC, $arr);
        } else if ($order == 5) {
            $sort = array_column($arr, "degree");
            array_multisort($sort, SORT_DESC, $arr);
        } else if ($order == 6) {
            $sort = array_column($arr, "degree");
            array_multisort($sort, SORT_ASC, $arr);
        } else {
            $sort = array_column($arr, "value_sort");
            array_multisort($sort, SORT_DESC, $arr);
        }
        return $arr;
    }
}