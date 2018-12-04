<?php 

namespace api\modules\v2\models;

use yii\base\Model;

class Code extends Model
{
	public static $codes = [
	    1011 =>"Parameter error ",
		1012 => "Category does not exist ",
		1013 => "No article under this category ",
		1014 => "No Data",
	    1015 => "Please enter the same password.",
		1016 => "Add data failure ",
		1017 => "This Email Address has been registered.",
		1018 => "Account does not exist ",
		1019 => "Your password is wrong.You can recover your password ",
		1020 => "Update data failure ",
	    1021 => "Login failure ",
	    1022 => "An email address must contain a single @",
	    1023 => "Illegal field",
	    1024 => "The address of the wallet is wrong",
	    1025 => "This account doesn’t exist.Enter a different account or get a new one.",
	    1026 => "You can add letter,number or symbol to make your password stronger.",
	    1027 => "You can add letter,number or symbol to make your password stronger.",
	    1028 => "The domain portion of the email address is invalid(the portion after the @:)",
	    1029 => "Please enter a value.",
	    1030 => "Please not release the same content",
	    1031 => "Please enter the valid email address.",
	    1032 => "Plesae login or register to release your content",
	    1033 => "Uploading picture files failed",
	    1034 => "Theme has a maximum of 60 characters",
	    1035 => "The content of the feedback is up to 1000 characters",
	    1036 => "Token failure",
	    1037 => "Username has existed",
	    1038 => "Signature error",
	    1039 => "Article data error",
	    1040 => "you can add maximum 3",
	    1041 => "Send email failure",
	    1042 => "Category has existed",
	    1043 => "Tag has existed",
	    1044 => "Signature is not right",
	    1045 => "Time expires",
	];

	public function code($code) {
		return self::$codes[$code];
	}
	
}