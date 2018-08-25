<?php

class mysql {
    private $link;

    /**
     * @param $host
     * @param $user
     * @param $pwd
     * @param $charset
     * @param $db_name
     * @return mysqli  Database connection object
     */
    function connect ($host, $user, $pwd, $charset, $db_name){
        $this->link = @mysqli_connect($host, $user, $pwd) or die ('Database connection failed'.mysqli_errno().':'.mysqli_error());
        mysqli_set_charset($this->link, $charset);
        mysqli_select_db($this->link, $db_name) or die('Failed to open the specified database');
        return $this->link;
    }

    /**
     * 插入记录的操作
     * @param array $array
     * @param string $table
     * @return boolean
     */
    function insert($table, $array){
        $keys = join(',',array_keys($array)); // 获取数组中的键名
        $values = "'".join("','", array_values($array))."'"; // 获取数组中的值并使用单引号
        $sql = "insert {$table}({$keys}) VALUES ({$values})"; // insert wp_options(option_name,option_value,autoload) VALUES ('test_ricky','5','yes')
        $res = mysqli_query($this->link, $sql);
        if($res){
            return mysqli_insert_id($this->link);
        }else{
            return false;
        }
    }

    /**
     * MYSQL更新操作
     * @param array $array
     * @param string $table
     * @param string $where
     * @return number|boolean
     */
    function update($table, $array, $where=null){
        $sets = "";
        foreach ($array as $key=>$val){
            $sets .= $key."='".$val."',";
        }
        $sets = rtrim($sets,','); 
        $where = $where==null?'':' WHERE '.$where;
        $sql = "UPDATE {$table} SET {$sets} {$where}";
        $res = mysqli_query($this->link, $sql);
        if ($res){
            return mysqli_affected_rows($this->link);
        }else {
            return false;
        }
    }

    /**
     * 查询一条记录
     * @param string $sql
     * @param string $result_type MYSQLI_ASSOC 关联数组
     * @return boolean
     */
    function get_row($sql, $result_type=MYSQLI_ASSOC){
        $result = mysqli_query($this->link, $sql);
        if ($result && mysqli_num_rows($result) > 0){
            return mysqli_fetch_array($result, $result_type);
        }else {
            return false;
        }
    }

}