<?php
// GENERATED CODE -- DO NOT EDIT!

namespace Com\Stamhe\Springboot\User\Proto;

/**
 */
class UserClient extends \Grpc\BaseStub {

    /**
     * @param string $hostname hostname
     * @param array $opts channel options
     * @param \Grpc\Channel $channel (optional) re-use channel object
     */
    public function __construct($hostname, $opts, $channel = null) {
        parent::__construct($hostname, $opts, $channel);
    }

    /**
     * 简单消息
     * @param \Com\Stamhe\Springboot\User\Proto\UserRequest $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     */
    public function UserInfo(\Com\Stamhe\Springboot\User\Proto\UserRequest $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.stamhe.springboot.user.proto.User/UserInfo',
        $argument,
        ['\Com\Stamhe\Springboot\Common\Proto\CommonReply', 'decode'],
        $metadata, $options);
    }

}
