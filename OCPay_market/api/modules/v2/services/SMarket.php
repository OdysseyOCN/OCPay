<?php
namespace api\modules\v2\services;

use Yii;
use api\modules\v2\models\Collect;
use api\modules\v2\models\Market;
use api\modules\v2\utils\Util;

class SMarket
{
    /**
     * 获取指定用户的收藏列表
     * @param $search 搜索词
     * @return array
     */
    function get_list($search, $user_id, $plat_type, $order) {
        $redis = Yii::$app->redis;
        $res = $redis->zrange("market", 0, -1);

        $info = [];
        if (!$res) {
            $time = Market::get_create_time();

            if ($search) {
                $info = Market::get_list_search_token($time, $search);
            } else {
                $info = Market::get_list_for_time($time);
            }
        } else {
            foreach($res as $key => $val) {
                $source = json_decode($val, true);
                if ($search) {
                    if (stripos($source["token"], $search) !== false) $info[] = $source;
                } else {
                    $info[] = $source;
                }
            }
        }

        // 处理最优
        $data = Collect::get_opt_col($user_id, $plat_type);
        $norm_col = $data["norm_col"];
        $opt_col = $data["opt_col"];

        // 获取代币发行量
        $max_supply = Util::get_max_supply();

        $arr = [];
        if ($info) {
            // 处理排序
            $res = [];
            foreach($info as $val) {
                if (isset($max_supply[$val["token"]])) {
                    $value = $max_supply[$val["token"]] * $val["close"];
                    $val["value_sort"] = $value;
                    if ($value / 1000000 > 1000) {
                        $value = round(($value / 1000000000), 3)."B";
                    } else {
                        $value = round(($value / 1000000), 3)."M";
                    }

                    $val["value"] = $value;
                } else {
                    continue;
                }
                $val["close"] = sprintf("%0.4f", $val["close"]);
                $val["degree"] = sprintf("%0.2f", $val["degree"]);
                $val["vol"] = $val["vol"] * $val["close"];
                $val["vol_format"] = number_format(sprintf("%0.2f", $val["vol"]));
                //$val["vol_format"] = number_format($val["vol"], 0);
                $val["collect_status"] = 0;
                if (isset($norm_col[$val["token"]."_".$val["exchange_name"]])) $val["collect_status"] = 1;

                $res[$val["token"]][] = $val;
            }

            // 处理最优
            foreach($res as $key =>$val) {
                $tem = $res[$key];
                //unset($tem[0]);
                if (isset($opt_col[$res[$key][0]["token"]])) $res[$key][0]["collect_status"] = 1;
                else $res[$key][0]["collect_status"] = 0;

                $res[$key][0]["child"] = array_values($tem);

                $arr[] = $res[$key][0];
                unset($res[$key]);
            }

            $arr = Util::get_market_sort($order, $arr);
        }

        if ($search) {
            $hot_search = HotSearch::get_list_for_search($search);
            if ($hot_search) {
                HotSearch::updateAll(["search_count" => $hot_search["search_count"] + 1], " token = '{$search}' ");
            } else {
                $hot = new HotSearch();
                $hot->token = $search;
                $hot->search_count = 1;
                $hot->save();
            }
        }
        return $arr;
    }
}