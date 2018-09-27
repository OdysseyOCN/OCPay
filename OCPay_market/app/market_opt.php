<?php

require_once "../util/db.php";
require_once "./Util.php";
require_once "./Log.php";
$log = new Log();
$_POST["url"] = "/app/market.php";
$log->writeLog($_POST);

$type = isset($_POST["type"])?$_POST["type"]:"";

$db = DB::getIntance();

$redis = new Redis();
$redis->connect("127.0.0.1", 6379);

if ($type == "add") { // 添加收藏
    add_collect($_POST, $db, $log, $code);
}

if ($type == "favor") { // 收藏列表
    favor_list($_POST, $db, $redis, $log, $code);
}

if ($type == "list") { // token列表
    query($_POST, $db, $redis, $log);
}

if ($type == "exchange") { // 交易所列表
    exchange_list($_POST, $db, $log);
}

if ($type == "search_init") { // 初始搜索
    search_init($db, $log);
}

$redis->close();

/**
 * 添加收藏
 *
 * @param $post 请求参数
 * @param $db 数据库连接对象
 * @param $log 日志
 * @param $code 返回码
*/
function add_collect($post, $db, $log, $code) {
    $user_id = isset($post["user_id"])?$post["user_id"]:0;
    $curr_token = isset($post["curr_token"])?$post["curr_token"]:"";
    if ($user_id == 0 || $curr_token == "") {
        echo json_encode(["code" => 1036, "msg" => $code[1036]]);
        return;
    }

    $exchange = isset($post["exchange"])?$post["exchange"]:"";
    $plat_type = isset($post["plat_type"])?$post["plat_type"]:1;
    // 0 取消收藏 1 收藏最优 2 普通收藏
    if ($exchange) {
        // 处理普通收藏
        $sql = "select ID, type from wp_collect where user_id = $user_id and token = '{$curr_token}' and exchange = '{$exchange}' and plat_type = $plat_type ";
        $col_type = 2;
    } else {
        // 处理最优收藏
        $sql = "select ID, type from wp_collect where user_id = $user_id and token = '{$curr_token}' and plat_type = $plat_type ";
        $col_type = 1;
    }
    $col = $db->get_row($sql);

    if ($col) {
        $c_type = $col["type"];
        if ($c_type == 1 || $c_type == 2) $c_type = 0;
        else $c_type = 1;

        $id = $col["ID"];
        $update = [
            "type" => $c_type
        ];
        $db->update("wp_collect", $update, " ID = $id ");
        $log->writeLog($update);
    } else {
        $add = [
            "user_id" => $user_id,
            "token" => $curr_token,
            "exchange" => $exchange,
            "type" => 1,
            "col_type" => $col_type,
            "plat_type" => $plat_type,
            "create_time" => time()
        ];
        $db->insert("wp_collect", $add);
        $log->writeLog($add);
    }
    echo json_encode(["code" => 200]);
}

/**
 * 收藏列表
 *
 * @param $post 请求参数
 * @param $db 数据库连接对象
 * @param $redis
 * @param $log 日志
 * @param $code 返回码
*/
function favor_list($post, $db, $redis, $log, $code) {
    $order = isset($post["order"])?$post["order"]:1;
    $user_id = isset($post["user_id"])?$post["user_id"]:0;
    $plat_type = isset($post["plat_type"])?$post["plat_type"]:1;
    if ($user_id == 0) {
        echo json_encode(["code" => 1011, "msg" => $code[1011]]);
        return;
    }
    $sql = "select token, exchange, col_type from collect where user_id = $user_id and type in (1, 2) and plat_type = $plat_type";
    $col = $db->get_result($sql);

    if ($col) {
        $sql = "select create_time from market order by create_time desc limit 1";
        $time = $db->get_var($sql);

        $market = $redis->zRange("market", 0, -1, true);
        $opt_info = []; // 最优
        $nor_info = [];

        if ($market){
            $o_info = [];
            foreach($market as $key => $val) {
                $source = json_decode($key, true);
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

        $res = [];
        $util = new Utils();
        $max_supply = $util->get_max_supply($db);
        foreach($col as $val) {
            $token = $val["token"];
            $exchange = $val["exchange"];
            $type = 1;
            if ($val["col_type"] == 1) {
                if (isset($opt_info[$token])) {
                    $info = $opt_info[$token];
                } else {
                    $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and token in ('{$token}') order by `close` limit 1";
                    $info = $db->get_row($sql);
                }
            } else {
                if (isset($nor_info[$exchange.$token])) {
                    $info = $nor_info[$exchange.$token];
                } else {
                    $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and token = '{$token}' and exchange = '{$exchange}' ";
                    $info = $db->get_row($sql);
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

        if ($res) $res = get_market_sort($order, $res);

        $log->writeLog($res);
        echo json_encode(["code" => 200, "data" => $res]);
        return ;
    }
    echo json_encode(["code" => 200, "data" => []]);
}

function query($post, $db, $redis, $log) {
    $order = isset($post["order"])?$post["order"]:1;
    $user_id = isset($post["user_id"])?$post["user_id"]:0;
    $search = isset($post["search"]) ? $post["search"] : "";
    $plat_type = isset($post["plat_type"])?$post["plat_type"]:1;

    //if ($search) $res = $redis->zRange("market_search", 0, -1, true);
    //else
    $res = $redis->zRange("market", 0, -1, true);

    $info = [];
    if (!$res) {
        $sql = "select create_time from market order by create_time desc limit 1";
        $time = $db->get_var($sql);

        if ($search) {
            $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and (currency = 'USD' or current = 'USDT') and token like '{$search}%' order by `vol` desc ";
            $info = $db->get_result($sql);
        } else {
            $sql = "select ID, exchange_name, token, currency, `close`, degree, vol from market where create_time = $time and (currency = 'USD' or current = 'USDT') order by `vol` desc ";
            $info = $db->get_result($sql);
        }
    } else {
        foreach($res as $key => $val) {
            $source = json_decode($key, true);
            if ($search) {
                if (stripos($source["token"], $search) !== false) $info[] = $source;
            } else {
                $info[] = $source;
            }
        }
    }

    // 处理最优
    $norm_col = [];
    $opt_col = [];
    if ($user_id) {
        $sql = "select token, exchange, col_type from collect where user_id = $user_id and type = 1 and plat_type = $plat_type";
        $col = $db->get_result($sql);

        if ($col) {
            foreach($col as $val) {
                if ($val["col_type"] == 1) $opt_col[$val["token"]] = 1;
                else $norm_col[$val["token"]."_".$val["exchange"]] = 1;
            }
        }
    }

    // 获取代币发行量
    $util = new Utils();
    $max_supply = $util->get_max_supply($db);

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

        $arr = get_market_sort($order, $arr);
    }

    if ($search) {
        $sql = "select token, search_count from hot_search where token = '{$search}' ";
        $hot_search = $db->get_row($sql);
        if ($hot_search) {
            $db->update("hot_search", [
                "search_count" => $hot_search["search_count"] + 1
            ], " token = '{$search}' ");
        } else {
            $db->insert("hot_search", [
                "token" => $search,
                "search_count" => 1
            ]);
        }
    }

    $log->writeLog($arr);
    echo json_encode(["code" => 200, "data" => $arr]);
}

function exchange_list($post, $db, $log) {
    $order = isset($post["order"])?$post["order"]:5;
    $search = isset($post["search"])?$post["search"]:"";
    if ($search) {
        $sql = "select exchange exchange_name, pair, vol vol_format, icon from exchange where exchange like '{$search}%' ";
        $info = $db->get_result($sql);

        $sql = "select token, search_count from hot_search where token = '{$search}' ";
        $hot_search = $db->get_row($sql);
        if ($hot_search) {
            $db->update("hot_search", [
                "search_count" => $hot_search["search_count"] + 1
            ], " token = '{$search}' ");
        } else {
            $db->insert("hot_search", [
                "token" => $search,
                "search_count" => 1
            ]);
        }
    } else {
        $sql = "select exchange exchange_name, pair, vol vol_format, icon from exchange order by vol desc";
        $info = $db->get_result($sql);
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

    $log->writeLog($info);
    echo json_encode(["code" => 200, "data" => $info]);
}

function search_init($db, $log) {
    $sql = "select token from hot_search limit 5";
    $info = $db->get_result($sql);
    if (!$info) $info = [];

    $log->writeLog($info);
    echo json_encode(["code" => 200, "data" => $info]);
}

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