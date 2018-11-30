<?php 

namespace api\modules\v1\models;

use yii\base\Model;

class Constant extends Model
{
	// 跨域允许域名
	public function cross_domain() {
		return '*.ocnex.net';
	}
}