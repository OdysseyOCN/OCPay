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

    /**
     * 获取一行记录 (一维数组)
     * @param $sql
     * @param string $type
     * @return mixed
     */
    public function get_row($sql, $type="assoc") {
        $query = $this->query($sql);
        $list[] = $this->getFormSource($query);
        return $list;
    }

    /**
     * 获取一条记录 前置条件通过资源获取一条记录
     * @param $query
     * @param string $type
     * @return mixed
     */
    public function getFormSource($query, $type = "assoc") {
        if (!in_array($type, array("assoc", "array", "row"))) {
            die("mysqli_query error");
        }

        $funcname = "mysqli_fetch_".$type;
        return $funcname($query);
    }

    /**
     * 获取多条数据 (二维数组)
     * @param $sql
     * @return array
     */
    public function get_all($sql) {
        $query = $this->query($sql);
        $list = array();
        while($r = $this->getFormSource($query)) {
            $list[] = $r;
        }
        return $list;
    }

    /**
     * 获取多条数据 (二维数组)
     * @param $table
     * @param $where
     * @param string $fields
     * @param string $order
     * @param int $skip
     * @param int $limit
     * @return array
     */
    public function selectAll($table, $where, $fields = '*', $order = '', $skip = 0, $limit = 1000) {
        if (is_array($where)) {
            foreach ($where as $key => $val) {
                if (is_numeric($val)) {
                    $condition = $key.'='.$val;
                } else {
                    $condition = $key.'{$val}';
                }
            }
        } else {
            $condition = $where;
        }

        if (!empty($order)) {
            $order = "order by ".$order;
        }

        $sql = "select $fields from $table where $condition $order limit $skip, $limit";
        $query = $this->query($sql);
        $list = array();
        while ($r = $this->getFormSource($query)) {
            $list[] = $r;
        }
        return $list;
    }

    /**
     * 添加数据
     * @param $table
     * @param $data
     * @return int|string
     */
    public function insert($table, $data) {
        // 遍历数组 得到每一个字段和字段的值
        $key_str = '';
        $v_str = '';
        foreach ($data as $key => $v) {
            // $key 的值是每一个字段s一个字段所对应的值
            $key_str .= $key.',';
            $v_str .= "'$v',";
        }
        $key_str = trim($key_str,",");
        $v_str = trim($v_str, ",");

        // 判断数据是否为空
        $sql = "insert into $table ($key_str) values ($v_str)";
        $this->query($sql);

        // 返回上一次增加产生ID值
        return $this->getInsertid();
    }

    /**
     * 删除单条数据
     * @param $table
     * @param $where
     * @return int
     */
    public function deleteOne($table, $where) {
        if (is_array($where)) {
            foreach ($where as $key => $val) {
                $condition = $key.'='.$val;
            }
        } else {
            $condition = $where;
        }

        $sql = "delete from $table where $condition";
        $this->query($sql);

        // 返回受影响行数
        return mysqli_affected_rows($this->link);
    }

    /**
     * 删除多条记录
     * @param $table
     * @param $where
     * @return int
     */
    public function deleteAll($table, $where) {
        if (is_array($where)) {
            foreach ($where as $key => $val) {
                if (is_array($val)) {
                    $condition = $key.' in ('.implode(",", $val).')';
                } else {
                    $condition = $key.'='.$val;
                }
            }
        } else {
            $condition = $where;
        }

        $sql = "delete from $table where $condition";
        $this->query($sql);

        // 返回受影响行数
        return mysqli_affected_rows($this->link);
    }

    /**
     * 更新记录
     * @param $table
     * @param $data
     * @param $where
     * @param int $limit
     * @return int
     */
    public function update($table, $data, $where, $limit = 0) {
        // 遍历数组 得到每一个字段和字段的值
        $str = '';
        foreach ($data as $key => $v) {
            $str .= "$key = '$v',";
        }
        $str = rtrim($str, ',');

        if (is_array($where)) {
            foreach ($where as $key => $val) {
                if (is_array($val)) {
                    $condition = $key.' in ('.implode(',', $val).')';
                } else {
                    $condition = $key.'='.$val;
                }
            }
        } else {
            $condition = $where;
        }

        if (!empty($limit)) {
            $limit = " limit ".$limit;
        } else {
            $limit = '';
        }

        // 修改sql语句
        $sql = "update $table set $str where $condition $limit";
        $this->query($sql);

        // 返回受影响的行娄
        return mysqli_affected_rows($this->link);
    }

}