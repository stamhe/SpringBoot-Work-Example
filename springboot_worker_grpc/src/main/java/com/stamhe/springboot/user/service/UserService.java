package com.stamhe.springboot.user.service;

import com.alibaba.fastjson.JSON;
import com.stamhe.springboot.common.proto.CommonReply;
import com.stamhe.springboot.user.model.UserModel;
import com.stamhe.springboot.user.proto.UserGrpc;
import com.stamhe.springboot.user.proto.UserRequest;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserService extends UserGrpc.UserImplBase {
	@Override
	public void userInfo(UserRequest request, StreamObserver<CommonReply> responseObserver) {

		UserModel userModel = new UserModel();
		userModel.setUser_id(request.getUserId());
		userModel.setName("Stam He");
		userModel.setCreateTime("2019-04-17 12:00:00");
		
		String json_data = JSON.toJSONString(userModel);
		
		CommonReply reply = CommonReply.newBuilder().setCode(0).setMessage("Success From UserService")
				.setData(json_data).build();
		
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
	}
}
