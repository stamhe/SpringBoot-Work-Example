<?php
require_once  __DIR__ . '/vendor/autoload.php';

use \Com\Stamhe\Springboot\User\Proto\UserClient;
use \Com\Stamhe\Springboot\User\Proto\UserRequest;

use \Com\Stamhe\Springboot\Common\Proto\CommonReply;

$start_time = microtime(true);

$obj = new UserClient('127.0.0.1:9090', [
    'credentials' => \Grpc\ChannelCredentials::createInsecure(),
    'timeout' => 1000,
]);


$req = new UserRequest();
$req->setUserId(10000);
$rsp = $obj->UserInfo($req)->wait();

$end_time = microtime(true);
printf("start_time = %s end_time = %s 【diff = %s】 ms\n", $start_time, $end_time, ($end_time - $start_time) * 1000.0);

list($rsp_data, $rsp_status) = $rsp;
var_dump($rsp_status);
var_dump($rsp_data->getCode());
var_dump($rsp_data->getMessage());
var_dump($rsp_data->getData());

