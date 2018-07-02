var ContentType = "application/json; charset=utf-8";
var AcceptType = "application/json; charset=utf-8";
/**
 * 日期格式化
 * @param fmt
 * @returns
 */
Date.prototype.format = function(fmt) { 
    var o = { 
       "M+" : this.getMonth()+1,                 //月份 
       "d+" : this.getDate(),                    //日 
       "h+" : this.getHours(),                   //小时 
       "m+" : this.getMinutes(),                 //分 
       "s+" : this.getSeconds(),                 //秒 
       "q+" : Math.floor((this.getMonth()+3)/3), //季度 
       "S"  : this.getMilliseconds()             //毫秒 
   }; 
   if(/(y+)/.test(fmt)) {
           fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
   }
    for(var k in o) {
       if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
   return fmt; 
}


var currentPage=1;
var pageSize=25;
var Main = {
	el:'#app',
    data() {
      return {
    	  loadingText:"拼命加载中",
    	  loading:true,
    	  height:500,
    	  tableData: [],
	      currentPage: currentPage,
	      pageSize:pageSize,
	      totalCount:0,
	      pickerOptions: {
	          shortcuts: [{
	            text: '最近一周',
	            onClick(picker) {
	              const end = new Date();
	              const start = new Date();
	              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
	              picker.$emit('pick', [start, end]);
	            }
	          }, {
	            text: '最近一个月',
	            onClick(picker) {
	              const end = new Date();
	              const start = new Date();
	              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
	              picker.$emit('pick', [start, end]);
	            }
	          }, {
	            text: '最近三个月',
	            onClick(picker) {
	              const end = new Date();
	              const start = new Date();
	              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
	              picker.$emit('pick', [start, end]);
	            }
	          }]
	       },
	      options: [{
              value: 1,
              label: '未支付'
          },{
              value: 2,
              label: '已支付'
          },{
              value: 3,
              label: '已取消'
          }],
          filter:{
        	  dataValue: '',
              errMsg:''
          },
          sapLogDialogVisible: false,
          ruleForm:{}
        }
    },
    methods: {
    	//过滤条件
    	selectValue(val){
    		this.currentPage = 1;
    		this.loading = true;
    		loadSapLog(this,true);
    	},
	    //双击列表数据
	    handleDBClick(row,column,cell, event){
	    	this.handleShow(cell,row);
	    },
	    //编辑按钮
        handleShow(index, row) {
	    	this.ruleForm = {
				  apiCode:row.apiCode,
	        	  syncID:row.syncID,
	        	  lastReplayDatetime:row.lastReplayDatetime?row.lastReplayDatetime.substring(0,19):"",
	        	  reqURL:row.reqURL,
	        	  reqBody:row.reqBody,
	        	  isReplay:row.isReplay,
	        	  hasErr:row.hasErr,
	        	  successMsg:row.successMsg,
	        	  errMsg:row.errMsg
	          }
	    	this.sapLogDialogVisible = true;
	    },
	    //每页显示多少数据
	    handleSizeChange(val) {
	    	this.pageSize = val;
	    	this.loading = true;
    		loadSapLog(this,true);
	    },
	    //当前页面
        handleCurrentChange(val) {
	    	this.currentPage = val;
	    	this.loading = true;
    		loadSapLog(this,true);
        }
    },
    //初始化前操作
    beforeCreate: function () {
    	loadSapLog(this,true);
    },
    created(){
    	var height = $(document).height()-140;
    	this.height=height;
    }
  };
var app = new Vue(Main);
/**
 * 加载订单数据
 */
function loadSapLog(thi,isLoading){
    var startTime = thi.filter?(thi.filter.dataValue?thi.filter.dataValue[0].format("yyyy-MM-dd 00:00:00"):null):null;
    var endTime = thi.filter?(thi.filter.dataValue?thi.filter.dataValue[1].format("yyyy-MM-dd 23:59:59"):null):null;
	jQuery.ajax({
    	url:"user/loadSapLog.do",
    	type:"POST",
    	cache:false,
    	data: JSON.stringify({
    		currentPage: thi.currentPage||currentPage,
            pageSize:thi.pageSize||pageSize,
            startTime: startTime,
            endTime: endTime,
            errMsg:thi.filter?thi.filter.errMsg:null
    	}),
    	contentType:ContentType,
    	headers: {
    	    Accept: AcceptType
    	},
    	dataType:"json",
    	success:function(data){
    		if(isLoading){
    			app.loading = false;
    		}
    		if(data.success){
    			app.tableData = data.result.data;
    			app.totalCount = data.result.count;
    		}else{
    			app.$alert(data.errorMessage, '提示',{
    				type:"error"
    			});
    		}
    	}
    });
}