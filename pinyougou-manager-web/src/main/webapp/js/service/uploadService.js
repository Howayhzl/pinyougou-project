app.service("uploadService",function ($http) {


    //上传文件
    this.uploadFile = function () {
        var formdata = new FormData();
        formdata.append('file',file.files[0]); //file为文件上传的名称
        return $http({
            method:'post',
            url:'../upload.do',
            data:formdata,
            headers:{'Content-Type':undefined},
            transformRequest:angular.identity
        })
    }
});