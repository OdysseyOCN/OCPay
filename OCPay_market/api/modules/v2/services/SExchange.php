<?php
namespace api\modules\v2\services;

use Yii;
use api\modules\v2\models\Collect;
use api\modules\v2\models\Market;
use api\modules\v2\utils\Util;

class SExchange
{
    /**
     * 获取交易所列表
     */
    function get_exchange_list($search, $order) {
        if ($search) {
            $info = Exchange::get_list_for_search($search);
            HotSearch::add_search($search);
        } else {
            $info = Exchange::get_list();
        }

        if ($info) {
            if ($order == 6) {
                $sort = array_column($info, "vol_format");
                array_multisort($sort, SORT_ASC, $info);
            } else if ($order == 3) {
                $sort = array_column($info, "pair");
                array_multisort($sort, SORT_DESC, $info);
            } else if ($order == 4) {
                $sort = array_column($info, "pair");
                array_multisort($sort, SORT_ASC, $info);
            }
            foreach($info as $key => $val) {
                $info[$key]["vol_format"] = "$".$val["vol_format"]."M";
            }
        } else {
            $info = [];
        }
        return $info;
    }
}