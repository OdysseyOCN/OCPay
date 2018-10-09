<?php
namespace api\modules\v1\models;

use Yii;
use yii\db\ActiveRecord;

class User extends ActiveRecord
{
    
    /**
     * @return string Active Record 类关联的数据库表名称
     */
    public static function tableName()
    {
        return '{{%users}}';
    }

    // 生成用户名
    public function get_username() {
	    $inv_arr = [1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

	    $invite = 'user';
	    for ($i = 0; $i < 8; $i ++) {
	        $invite .= $inv_arr[array_rand($inv_arr)];
	    }
	    $sql = "select user_login from wp_members where user_login = '{$invite}' ";
	    $id = Yii::$app->db->createCommand($sql)->queryOne();
	    if (isset($username["user_login"])) {
	        $invite = self::get_username();
	    }
	    return $invite;
	}

	// 根据邮箱获取用户信息
	public function get_user_for_email($email) {
	    $sql = "select ID, user_login, user_pass, user_registered from wp_users where user_email = '{$email}' ";
	    $user = Yii::$app->db->createCommand($sql)->queryOne();
	    return $user;
	}

	// 根据用户名获取用户信息
	public function get_user_for_username($username) {
		$sql = "select ID, user_login, user_email, user_pass, user_registered from wp_users where user_login = '{$username}' ";
		return Yii::$app->db->createCommand($sql)->queryOne();
	}

}