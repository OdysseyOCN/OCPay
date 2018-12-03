<?php

namespace api\modules\v2\controllers;

use yii;
use yii\rest\ActiveController;
use yii\web\Response;
use yii\filters\Cors;
use yii\helpers\ArrayHelper;
use api\modules\v2\models\Constant;
use api\modules\v2\models\Log;

class BaseController extends ActiveController
{
	public $modelClass = 'api\modules\v2\models\User';

	public function behaviors()
	{
	    return ArrayHelper::merge([
	        [
	            'class' => Cors::className(),
	            'cors' => [
	                'Origin' => [Constant::cross_domain()],
	                'Access-Control-Request-Headers' => [Constant::cross_domain()],
	                'Access-Control-Request-Method' => ['GET', 'POST', 'OPTIONS'],
	            ],
	        ],
	        'access' => [
	    	    'class' => 'api\modules\v2\filters\AccessFilter',
	    	],
	    ], parent::behaviors());
	}
}