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