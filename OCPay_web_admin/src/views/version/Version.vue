<template>
<div>
  <h3>Version</h3>
     <hr/>
  <el-form  :inline="true" :model="formInline" class="demo-form-inline wallet">
        <el-col class="username" :span="22">
            <el-form-item  label="查询">
                <el-input v-model="formInline.id" placeholder="请输入查询ID"  @change="onSubmit"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="2">
            <el-button type="primary" @click="onSubmit" style="display:none;">查询</el-button>
             <el-button type="primary" @click="onSubmitForm">清空</el-button>
        </el-col>
        <div style="clear:both;"></div>
    </el-form>  
   <el-form  class="demo-form-inline wallet">
        <el-button type="primary" @click="add">ADD</el-button>
    </el-form>  
  <hr/>
  <el-table
    :data="tableData"
    border
    style="width: 100%">
    <el-table-column
      prop="No"
      label="No."
      width="150">
      <template slot-scope="scope1">             
					{{scope1.$index+1+10*(currentPage-1)}}
				</template>  
    </el-table-column>
    <el-table-column
      prop="id"
      label="ID"
      width="150">
    </el-table-column>
    <el-table-column
      prop="versionId"
      label="版本ID"
      width="150">
    </el-table-column>
    <el-table-column
      prop="versionName"
      label="版本名"
      width="150">
    </el-table-column>
     <el-table-column
      prop="content"
      label="版本内容"
      width="150">
    </el-table-column>
    
    <el-table-column
      prop="status"
      label="status"
      width="120">
        <template slot-scope="scope">
                    <span v-if="scope.row.status==1">未发布</span>
                     <span v-if="scope.row.status==2">已发布</span>
		  	</template>
    </el-table-column>
    <el-table-column
      prop="createBy"
      label="创建者"
      width="150">
    </el-table-column>
    <el-table-column
      prop="updateBy"
      label="更新者"
      width="150">
    </el-table-column>
     <el-table-column
      prop="createTime"
      label="创建时间"
      width="180">
        <template slot-scope="scope">
                     {{scope.row.createTime | time}}
		  	</template>
    </el-table-column>
    <el-table-column
      prop="updateTime"
      label="更新时间"
      width="180">
         <template slot-scope="scope">
                     {{scope.row.updateTime | time}}
		  	</template>
    </el-table-column>
    <el-table-column label="Operation"  width="180"  >
				<template slot-scope="scope"
        >
                 <el-button size="small" @click="edit(scope.$index, scope.row)">EDIT</el-button>
                 <el-button size="small" @click="Delete( scope.row)">Delete</el-button>     
				</template>
		</el-table-column>   
  </el-table>
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
<!-- 添加 -->
  <el-dialog
         title=""
        :visible.sync="dialogVisible"
        class="Veradd"
         width="40%">
         <el-form ref="form" :model="form" label-width="100px">
           <el-form-item label="版本ID：">
               <el-input v-model="form.versionId"></el-input>
           </el-form-item>
           <el-form-item label="版本名：">
               <el-input v-model="form.versionName"></el-input>
           </el-form-item>
         
           <el-form-item class="banbe"  label="发布状态：">
               <el-select class="banben" v-model="form.region" placeholder="发布状态">
                     <el-option label="未发布" value="1"></el-option>
                     <el-option label="已发布" value="2"></el-option>
                </el-select>
            </el-form-item>
           <el-form-item label="版本内容：">
               <el-input v-model="form.content"></el-input>
           </el-form-item>
             <div class="button"><el-button type="primary" @click="submitadd">添加</el-button></div>                 
           </el-form>
</el-dialog>
<!-- 编辑 -->

<el-dialog
         title=""
        :visible.sync="dialogVisible1"
        class="Veradd"
         width="30%">
         <el-form ref="form" :model="form1" label-width="100px">
           <!-- <el-form-item label="id">
               <el-input v-model="form1.id"></el-input>
           </el-form-item> -->
          <el-form-item label="版本ID：">
               <el-input v-model="form1.versionId"></el-input>
           </el-form-item>
           <el-form-item label="版本名：">
               <el-input v-model="form1.versionName"></el-input>
           </el-form-item>
             
           <el-form-item  label="发布状态：">
               <el-select  class="banben" v-model="form1.status1" v-if="form1.status==1" placeholder="未发布">
                     <el-option label="未发布" value="1"></el-option>
                     <el-option label="已发布" value="2"></el-option>
                </el-select>
                   <el-select v-model="form1.status1" v-if="form1.status==2" placeholder="已发布">
                     <el-option label="未发布" value="1"></el-option>
                     <el-option label="已发布" value="2"></el-option>
                </el-select>
            </el-form-item>
           <!-- <el-form-item label="发布状态：">
               <el-input v-model="form1.status"></el-input>
           </el-form-item> -->
           <el-form-item label="版本内容：">
               <el-input v-model="form1.content"></el-input>
           </el-form-item>
             <div class="button"><el-button type="primary" @click="submitedit">编辑</el-button></div>
           </el-form>
        
</el-dialog>
</div>
  
</template>

<script>
import { versionList,versionAdd,versionEdit,versionDelete } from '../../api/api';
  export default {
    data() {
      return {
        tablesonData:[],
        tableData: [],
         dialogVisible: false ,
         dialogVisible1: false ,
        //  dialogVisible:false,
         form:{},
          formInline:{},
           
          form1:{},
          pagesize: 10,
        //当前页码 
        currentPage: 1,
        //默认数据总数
        totalCount: 20,
      }
    },
     mounted(){
        
        this.getcoin(this.currentPage);
    },
     methods:{
       getcoin(num){
           
            let para={
                // "id":"4",
               "pageNum":"1",
              "pageSize":"10" }
               if(this.formInline.id!=''){
                 para={
                     "id":this.formInline.id,    
                     "pageNum":"1",
                     "pageSize":"10"
                            }
                     }     
            versionList(para).then(data=>{
                // console.log(data.data.list)
                this.tableData=data.data.list;
                this.pagesize=data.data.per;
                this.totalCount=data.data.per*data.data.pages;
            })
        },


        // 添加
        add(index,row){
                this.dialogVisible=true;      
        },
         submitadd(){
          
           let para={
           "versionId":this.form.versionId,
           "versionName":this.form.versionName,
           "content":this.form.content,  
           "status":this.form.status,
         }
             versionAdd(para).then(data=>{
                // console.log(data)
                this.dialogVisible=false;
                this.getcoin(this.currentPage);
                  // this.$message({
                  //   message: data.data.message,
                  //   type: 'success'
                  // });        
            })
        },



      
        // 编辑
        edit(index,row){
            this.dialogVisible1=true;
            this.form1=row;
        //    this.$store.state.count=row;
        },

        submitedit(){    
           let para={
              "id":this.form1.id,
              "versionId":this.form1.versionId,
              "versionName":this.form1.versionName,
              "content":this.form1.content,  
               "status":this.form1.status1, 
         }
             versionEdit(para).then(data=>{
                // console.log(data)
                this.dialogVisible1=false;
                this.getcoin(this.currentPage);
                  // this.$message({
                  //   message: data.data.message,
                  //   type: 'success'
                  // });        
            })
        },
         // 删除数据
        Delete(row){
         
           let para={
             "id":row.id,      
            }
          versionDelete(para).then(data=>{
                // console.log(data)
                this.getcoin(this.currentPage);    
            })     

        },
         // 查询
        onSubmit(){
           this.getcoin(this.currentPage);
        },
        // 清空
        onSubmitForm(){
            this.formInline.id=''
           
            this.getcoin(this.currentPage);
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

.Veradd .el-dialog--small{
width: 500px;
}

.banben{
  width: 300px;
}
</style>
