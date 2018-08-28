<?php

class Log {

    private $maxsize = 4096000; 

    public function writeLog($msg) {
        $filename = __DIR__."/log/ocp.log";
        $res = [];
        $res["msg"] = $msg;
        $res["logtime"] = date("Y-m-d H:i:s", time());
        $date = date("Y_m_d_H", time());

        if (file_exists($filename) && abs(filesize($filename)) > $this->maxsize) {
            $newfilename = dirname($filename).'/'.$date.'-'.basename($filename);
            rename($filename, $newfilename);
        }

        $content = json_encode($res).PHP_EOL;
        file_put_contents($filename, $content, FILE_APPEND);
    }
}