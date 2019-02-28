<?php
namespace api\modules\v2\services;

use Yii;
use api\modules\v2\models\Exchange;
use api\modules\v2\models\HotSearch;
use api\modules\v2\utils\Util;

class SExchange
{
    /**
     * 获取交易所列表
     *
     * @param $search 搜索词
     * @param $order 排序
     */
    function get_exchange_list($search, $order) {
        if ($search) {
            $info = Exchange::get_list_for_search($search);
            HotSearch::add_search($search);
        } else {
            $info = Exchange::get_list();
        }

        if ($info) {
            $info = Util::get_exchange_sort($order, $info);
        } else {
            $info = [];
        }
        return $info;
    }
}