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

$redis->close();