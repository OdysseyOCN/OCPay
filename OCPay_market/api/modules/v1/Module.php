<?php
namespace api\modules\v1;

use yii\filters\auth\HttpBasicAuth;
use yii\filters\auth\QueryParamAuth;
use yii\filters\auth\CompositeAuth;
use yii\filters\auth\HttpBearerAuth;
use yii\filters\RateLimiter;
/**
 * iKargo API V1 Module
 * 
 * @author Budi Irawan <budi@ebizu.com>
 * @since 1.0
 */
class Module extends \yii\base\Module
{
    public $controllerNamespace = 'api\modules\v1\controllers';

   public function init()
   {
       parent::init();
       //\Yii::$app->user->enableSession = false;
   }

//    public function behaviors()
//    {
//        $behaviors = parent::behaviors();
//        $behaviors['rateLimiter'] =[
//            'class' => RateLimiter::className(),
//        ];
//        $behaviors['authenticator'] = [
//            'class' => CompositeAuth::className(),
//            'authMethods' => [
//             //   HttpBasicAuth::className(),
//                HttpBearerAuth::className(),
//              //  QueryParamAuth::className(),
//            ],
//        ];
//        return $behaviors;
//    }
}