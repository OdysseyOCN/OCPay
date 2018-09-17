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

$redis->close();

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