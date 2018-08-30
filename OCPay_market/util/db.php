<?php

// 数据库连接类
class DB {
    private static $dbcon = false;
    private $host;
    private $user;
    private $pass;
    private $db;
    private $charset;
    private $link;

    private function __construct() {
        $this->host = 'localhost';
        $this->user = '';
        $this->pass = '';
        $this->db = '';
        $this->charset = 'utf8';

        // 连接数据库
        $this->db_connect();

        // 选择数据库
        $this->db_usedb();
        // 设置字符集
        $this->db_charset();
    }

    // 连接数据库
    private function db_connect() {
        $this->link = mysqli_connect($this->host, $this->user, $this->pass);
        if (!$this->link) {
            echo "数据库连接失败<br/>";
            echo "错误编码".mysqli_errno($this->link)."<br/>";
            echo "错误信息".mysqli_error($this->link)."<br/>";
            exit;
        }
    }

    // 设置字符集
    private function db_charset() {
        mysqli_query($this->link, "set names {$this->charset}");
    }

    // 选择数据库
    private function db_usedb() {
        mysqli_query($this->link, "use {$this->db}");
    }

    // 私有的克隆
    private function __clone() {
        die("clone is not allowed");
    }

    // 公用的静态方法
    public static function getIntance() {
        if (self::$dbcon == false) {
            self::$dbcon = new self;
        }
        return self::$dbcon;
    }

    /**
     * 执行sql语句的方法
     * @param $sql
     * @return bool|mysqli_result
     */
    public function query($sql) {
        $res = mysqli_query($this->link, $sql);
        if (!$res) {
            echo "sql语句执行失败<br>";
            echo "错误编码是".mysqli_errno($this->link)."<br>";
            echo "错误信息是".mysqli_error($this->link)."<br>";
        }
        return $res;
    }

        // 获得最后一条记录id
    public function getInsertid() {
        return mysqli_insert_id($this->link);
    }

    /**
     * 获取单个值
     * @param $sql
     * @param int $result_type
     * @return bool|void
     */
    public function get_var($sql, $result_type=MYSQLI_NUM) {
        $result = $this->query($sql);
        if ($result && mysqli_num_rows($result) > 0){
            $row =  mysqli_fetch_array($result, $result_type);
            mysqli_free_result($result);
            return $row[0];
        }else {
            return false;
        }
    }
}