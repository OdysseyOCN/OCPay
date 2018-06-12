<template>
  <div>
    <h3>  </h3>
    <hr/>
    
    <div class="table">
        <el-table
            :data="tableData"
            border
            max-height="550"
            style="width: 100%">
            <!-- <el-table-column
            width="id"
            label="id">
               
            </el-table-column> -->
              <el-table-column
            prop="theme"
            label="主题">
            </el-table-column>
            <el-table-column
            prop="description"
            label="描述">
            </el-table-column>
            <el-table-column
            prop="email"
            label="email">
            </el-table-column>
            
            <el-table-column
            prop="img1"
            label="图片1">
          <template slot-scope="scope">
                 <img :src="scope.row.img1" alt="">
		 	   </template>
            </el-table-column>
              <el-table-column
            prop="img2"
            label="图片2">
           <template slot-scope="scope">
                 <img :src="scope.row.img2" alt="">
		 	   </template>
            </el-table-column>
             <el-table-column
            prop="img3"
            label="图片3">
              <template slot-scope="scope">
                 <img :src="scope.row.img3" alt="">
		 	   </template>
           
            </el-table-column>
            <el-table-column
            prop="createTime"
            label="创建时间">
            <template slot-scope="scope">
                    <span >{{ scope.row.createTime | time }}</span>
                </template>
            </el-table-column>
            <el-table-column
            prop="updateTime"
            label="更新时间">
            <template slot-scope="scope">
                    <span >{{ scope.row.updateTime | time }}</span>
                </template>
            </el-table-column>
           
        </el-table>
    </div>
    <div class="block" style="text-align:center;margin-top:20px;">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pagesize"
          layout=" prev, pager, next"
          :total="totalCount">
        </el-pagination>
      </div>
    
  </div>
</template>

<script>
import { FeedbackUser } from '../../api/api';
export default {
    data() {
    return {
        formInline: {
         beginTime:'',
         endTime:'',
         id:'',
         txHash:''
        },
        platform:[],
        tableData: [],
        //默认每页数据量
        pagesize: 10,
        //当前页码 
        currentPage: 1,
        //默认数据总数
        totalCount: 20,

    };
    },
    mounted(){
        
        this.getcoin(this.currentPage);
    },
    methods: {
       
        getcoin(num){
           
            let para={ "pageNum":"1", "pageSize":"10"}
            
            FeedbackUser(para).then(data=>{
                console.log(data.data.list)
                this.tableData=data.data.list;
                this.pagesize=data.data.per;
                this.totalCount=data.data.per*data.data.pages;
            })
        },
       
        handleCurrentChange(val){
            // console.log(val)
            this.currentPage=val;
            this.getcoin(this.currentPage);
        },
       
       
    }
}

</script>
<style>
.wallet{
    padding: 20px;
}
.table{
    margin-top: 30px;
}
.line{
    text-align: right;
    padding-right: 5px;
}
.add{
    padding-left: 5px;
}
.add1{
    padding-left: 15px;
}
.secret{
    text-align: right;
    padding-right: 5px;
}
.erweima{
    padding-left: 20px;
}
.el-button+.el-button{
    margin-left: 2px;
}
.img2{
width: 50%;
height: 100%;
}
</style>
