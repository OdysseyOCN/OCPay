<?php

// * * * * * /usr/bin/php /var/www/OCPay_market/fetch_market_data.php

require_once "../util/config.php";
require_once "../util/functions.php";
$host = $config["host"];
$user = $config["user"];
$pass = $config["password"];
$charset = $config["charset"];
$db_name = $config["dbname"];

$db = new mysql();
$link = $db->connect($host, $user, $pass, $charset, $db_name);

$redis = new Redis();
$redis->connect("127.0.0.1", 6379);

$exchange = [
    "OKEx",
    'Huobipro',
    'Binance',
    'Bitfinex'
];

$redis->zRemRangeByRank("market", 0, -1);
$redis->zRemRangeByRank("market_search", 0, -1);
$time = time();
foreach($exchange as $value) {
    $url = '/api/v1/ticks/'.$value.'?unit=usd';
    $ch = curl_init();

    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    $curlRes = curl_exec($ch);
    curl_close($ch);

    $content = json_decode($curlRes, true);

    if (is_array($content)) {
        $pair = count($content);
        $vol = 0;
        foreach($content as $val) {
            if ($val["base"] == "VEN") continue;
            $market = [
                'ticker' => $val['ticker'],
                'exchange_name' => $val['exchangeName'],
                'token' => $val["base"],
                'currency' => $val["currency"],
                'close' => $val["close"],
                "degree" => $val["degree"],
                'vol' => $val["vol"],
                "create_time" => $time
            ];
            if ($val["currency"] == "USD" || $val["currency"] == "USDT") $redis->zAdd("market", $val["ticker"], json_encode($market));
            $redis->zAdd("market_search", $val["ticker"], json_encode($market));
            $vol += $val["vol"];
        }

        if ($vol) $vol = round(($vol / 1000000), 2);
        if ($value == "Huobipro") $value = "Huobi";

        $db->update("exchange", [
            'pair' => $pair,
            'vol' => $vol,
            'create_time' => time()
        ], " exchange = '{$value}' ");
    }
}

$redis->close();
$db->close();