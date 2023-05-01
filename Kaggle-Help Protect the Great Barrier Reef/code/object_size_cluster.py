# This Python 3 environment comes with many helpful analytics libraries installed
# It is defined by the kaggle/python Docker image: https://github.com/kaggle/docker-python
# For example, here's several helpful packages to load

import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
train = pd.read_csv('../input/tensorflow-great-barrier-reef/train.csv')

data = pd.read_csv('../input/tensorflow-great-barrier-reef/train.csv')
data.drop(data[data.annotations =="[]"].index,axis=0,inplace=True)
data["annotations"] = data["annotations"].apply(lambda x:eval(x))
w1 = []
w2 = []
h1 = []
h2 = []
for index,dict_List in enumerate(data["annotations"]):
    for size_dict in dict_List:
        if size_dict["width"]<=size_dict["height"]:
            w1.append(size_dict["width"])
            h1.append(size_dict["height"])
        else:
            w2.append(size_dict["width"])
            h2.append(size_dict["height"])


for q in [0.1, 0.25, 0.40, 0.50, 0.6, 0.75, 0.9]:
    for box in [w1, h1, w2, h2]:
        print(int(pd.Series(box).quantile(q)), end=', ')
    print()


import pandas as pd
import numpy as np
import copy
import cv2
from sklearn.cluster import DBSCAN
from sklearn import metrics
import torch
import time
from sklearn.datasets import make_blobs
from sklearn.preprocessing import StandardScaler

class ObjectSizeCluster:
    def __init__(self,datapath):
        self.path = datapath
        self.box_size_data = self.get_box_size(datapath)
        self.db = self.DBSCAN(self.box_size_data,eps=0.04,min_samples=10)
        self.cluster_hw = None

    def get_box_size(self,datapath):
        data = pd.read_csv(datapath)
        data.drop(data[data.annotations =="[]"].index,axis=0,inplace=True)
        data["annotations"] = data["annotations"].apply(lambda x:eval(x))
        box_size_data = []
        for index,dict_List in enumerate(data["annotations"]):
            for size_dict in dict_List:
                if size_dict["width"]>size_dict["height"]: #修改可获取 h>w或者h<w
                    box_size_data.append([size_dict["height"],size_dict["width"]])
        return np.asarray(box_size_data).round(6)

    def DBSCAN(self,X,eps=0.2,min_samples=10):
        X_min = X.min(0)
        X_max = X.max(0)
        X = (X-X_min)/(X_max-X_min)
        db = DBSCAN(eps=eps, min_samples=min_samples).fit(X)
        print("Silhouette Coefficient: %0.3f" % metrics.silhouette_score(X, db.labels_))
        return db
    # Plot result
    def plot_cluster(self,):
        X = self.box_size_data
        labels = self.db.labels_
        core_samples_mask = np.zeros_like(self.db.labels_, dtype=bool)
        core_samples_mask[self.db.core_sample_indices_] = True
        import matplotlib.pyplot as plt

        # Black removed and is used for noise instead.
        unique_labels = set(labels)
        n_clusters = len(set(labels)) - (1 if -1 in labels else 0)
        colors = [plt.cm.Spectral(each) for each in np.linspace(0, 1, len(unique_labels))]
        for k, col in zip(unique_labels, colors):
            if k == -1:
                # Black used for noise.
                col = [0, 0, 0, 1]

            class_member_mask = labels == k

            xy = X[class_member_mask & core_samples_mask]

            plt.plot(
                xy[:, 0],
                xy[:, 1],
                "o",
                markerfacecolor=tuple(col),
                markeredgecolor="k",
                markersize=14,
            )

            xy = X[class_member_mask & ~core_samples_mask]
            plt.plot(
                xy[:, 0],
                xy[:, 1],
                "o",
                markerfacecolor=tuple(col),
                markeredgecolor="k",
                markersize=6,
            )
        plt.xlabel("height")
        plt.ylabel("width")
        plt.title("Estimated number of clusters: %d" % n_clusters)
        plt.show()
        plt.close()
        df_area_size = pd.DataFrame(self.box_size_data)
        # df_area_size = df_area_size.qcut(n=50,labels=False)
        print(df_area_size.head())
        n_bin = 100
        bin = []
        df_area_size[0].hist(bins=20)
        plt.show()

    def get_cluster_size(self,n_max=0.4,n_min=0.05):
        """
        利用正常点，得到大多数的box尺寸
        """
        X = self.box_size_data
        labels = self.db.labels_
        core_samples_mask = np.zeros_like(self.db.labels_, dtype=bool)
        core_samples_mask[self.db.core_sample_indices_] = True
        X_index = ((labels != -1) & core_samples_mask)
        X_cluster = X[X_index]
        h_cluster,w_cluster = np.sqrt(X_cluster[:,0]*X_cluster[:,1]),np.sqrt(X_cluster[:,0]/X_cluster[:,1])
        h_cluster.sort(),w_cluster.sort() #排序
        max_split_start,max_split_end = int(len(h_cluster)*n_max),int(len(h_cluster)*min(n_max+0.1,1))
        min_split_start,min_split_end = int(len(h_cluster)*n_min),int(len(h_cluster)*min(n_min+0.1,1))

        h_max,w_max = h_cluster[-max_split_end:-max_split_start],w_cluster[-max_split_end:-max_split_start]
        h_min,w_min = h_cluster[min_split_start:min_split_end],w_cluster[min_split_start:min_split_end]
        h_max,w_max,h_min,w_min = np.mean(h_max),np.mean(w_max),np.mean(h_min),np.mean(w_min)
        return h_max,w_max,h_min,w_min

class CropResolutionPic():
    """
    对dataloader加载后，输入yolox之前的数据进行处理，需修改trainer.py中prefetcher接口，或则直接写一个新的接口覆盖。
    注意如果所有图片的输入尺寸是一致的，那么restore_para只需要一个就行，不需要重复计算。这里的代码考虑的更普遍的情况，对
    于海星题代码那么restore_para只需要一个就行参数计算可以简化。
    """
    def __init__(self,size = [720,1280],max_box_size= None,min_box_size=None):
        self.size = np.array(size)
        self.max_box_size = np.array(max_box_size) if max_box_size else self.size/60#如果不设置最小尺寸，就默认为图片输入尺寸的1/60
        self.min_box_size = np.array(min_box_size) if min_box_size else self.size/15#如果不设置最大尺寸，就默认为图片输入尺寸的1/15
        #考虑到Crop主要为了提高分辨率捕捉小物体，所以对max_box_size的大小不做严格要求，这一点在get_cluster_size的默认参数中有体现
        self.margin,self.window_size,self.cropNum,self.finetune_size = self.get_crop_para()
    def get_crop_para(self,max_cropNum=5):
        margin = (self.max_box_size//2).astype(np.int8)
        window_size = (self.min_box_size*10).astype(int)
        cropNum = int(max(self.size/window_size))
        cropNum = min(max_cropNum,cropNum)
        finetune_size = np.empty_like(self.size)
        finetune_size[0] = self.size[0] + cropNum-self.size[0]%cropNum
        finetune_size[1] = self.size[1] + cropNum-self.size[1]%cropNum
        window_size[0] = finetune_size[0]//cropNum
        window_size[1] = finetune_size[1]//cropNum
        return margin,window_size,cropNum,finetune_size
    def adaptive_finetune(self,img,boxes):#这个调节函数的的缩放维度还需要确认,当盒子为空的时候，不需要超分辨率，这是训练时要考虑的问题
        r = self.finetune_size /self.size
        finetune_img = cv2.resize(img,(self.finetune_size[1],self.finetune_size[0]))
        if len(boxes):#boxes非空才会计算
            boxes[:,0::2] *= r[1]#x乘以宽度方向缩放系数
            boxes[:,1::2] *= r[0]#y乘以高度向缩放系数
            #添加对图像周边添加margin大小的padding
            margin_h,margin_w = self.margin[0],self.margin[1]
            finetune_img = cv2.copyMakeBorder(finetune_img,margin_h,margin_h,margin_w,margin_w,cv2.BORDER_CONSTANT, value=(0,0,0))
            boxes[:,0] = boxes[:,0]+margin_w
            boxes[:,1] = boxes[:,1]+margin_h
            boxes[:,2] = boxes[:,2]+margin_w
            boxes[:,3] = boxes[:,3]+margin_h
            finetune_img = np.ascontiguousarray(finetune_img)
        resize = self.window_size+2*self.margin
        scale = resize/self.size
        resize_img = cv2.resize(img,dsize=(resize[1],resize[0]))
        return finetune_img,resize_img,boxes,scale

    def get_crop_data(self,img,boxes):#要注意空盒子,次子程序运行工一次约耗时0.015秒
        finetune_img,resize_img,boxes,scale = self.adaptive_finetune(img,boxes)#将原图尺寸调整到window_size整数倍，并添加margin_padding
        h,w = self.window_size*self.cropNum+2*self.margin
        assert finetune_img.shape[:-1] == (h,w)

        #求出每个patch图像左上角的角点(patch_x0,patch_y0)
        patch_x1 = np.array([self.window_size[0]*i for i in range(self.cropNum)])
        patch_y1 = np.array([self.window_size[1]*i for i in range(self.cropNum)])

        corner_coor1 = np.array(np.meshgrid(patch_x1,patch_y1)).transpose(2,1,0)
        corner_coor2 =corner_coor1+2*self.margin+self.window_size
        corner_coor = np.concatenate((corner_coor1,corner_coor2,),axis=-1)
        corner_coor = corner_coor.reshape(-1,4)
        crop_imgset = []
        for i in range(corner_coor.shape[0]):
            broad_win_corner0 = corner_coor[i,:]
            win_x1,win_y1,win_x2,win_y2 = broad_win_corner0[0],broad_win_corner0[1],broad_win_corner0[2],broad_win_corner0[3]
            crop_imgset.append(finetune_img[win_x1:win_x2,win_y1:win_y2].copy())

        crop_imgset.append(resize_img)
        restore_para = np.zeros((corner_coor.shape[0]+1,4))#用列表比较好，混合精度浪费内存
        restore_para[:-1,:] = corner_coor
        restore_para[-1,:] = np.array([scale[0],scale[1],self.margin[0],self.margin[1]])#[scale_h,scale_w,margin_h,margin_w]
        return crop_imgset,boxes,restore_para
    def get_batch_crop(self,imgs,boxes):#这里需要根据实际输入修改

        N,_,_,C = imgs.shape
        H,W = self.window_size+2*self.margin
        crop_imgset_batch = None
        boxes_batch = []
        restore_para_batch = []
        for i,img in enumerate(imgs) :
            t1 = time.time()
            crop_imgset,boxes_,restore_para = self.get_crop_data(img,boxes[i])
            t2 = time.time()
            crop_imgset = torch.as_tensor(crop_imgset)#这里图片没问题

            if crop_imgset_batch==None:
                crop_imgset_batch = copy.deepcopy(crop_imgset)
            else:

                crop_imgset_batch = torch.cat([crop_imgset_batch,crop_imgset],dim=0)

            boxes_batch.append(boxes_)
            restore_para_batch.append(restore_para)

            print(t2-t1)
        crop_imgset_batch = crop_imgset_batch.view(-1,H,W,C)#[N,cropNum**2+1,H,W,C]->[N*cropNum**2+1,H,W,C]

        return crop_imgset_batch,boxes_batch,restore_para_batch

class RestoreBoxes:
    """
    进行预测结果的处理时，要考虑crop图和resize图的区别，resize图要考虑缩放系数。
    1.删除面积过小的盒子在nms中应该有，这里不写了；
    2.删除高宽比异常的盒子:根据聚类结果高宽比绝大多数在（0.2-2.5）
    3.删除crop图中靠近边缘的大目标和90%面积的大目标，大目标由大图预测，防止目标截断；
    4.将boxes坐标还原，修正坐标超出框图的坐标
    """

    def __init__(self,boxes,restore_para):
        #boxes#[B*(CropNum+1),n]->B是batch_size,CropNum是裁剪数量,n是盒子预测数量
        self.restore_para = restore_para
        self.B_N = len(boxes)
        self.B = len(restore_para)
        assert self.B_N%self.B == 0
        boxes = self.delete_by_area_center(boxes)
        self.boxes = self.mdify_boxes_coor(boxes)

    def delete_by_area_center(self,boxes,area=None):
        """
        按照cropPic预测小目标，resizePic预测大目标的原则，按照面积删除cropPic中的大目标和resizePic中的小目标
        """
        B = self.B
        N = self.B_N//B
        keep_boxes = []
        for batch in range(B):
            scale_h,scale_w,margin_h,margin_w = self.restore_para[batch][-1]
            window_x1,window_y1 = margin_w,margin_h
            window_x2,window_y2 = self.restore_para[batch][0][2]-margin_w,self.restore_para[batch][0][3]-margin_h
            area = area if area else margin_h*margin_w*4#默认取值与margin相关，因为margin本身就是用来预测小物体的
            for cropboxes_index in range(N):
                boxes_idx = batch*N + cropboxes_index
                temp_boxes = boxes[boxes_idx]
                temp_area = (temp_boxes[:,2]-temp_boxes[:,0])*(temp_boxes[:,3]-temp_boxes[:,1])

                if cropboxes_index == N-1:
                    #这里是resize后的大图预测结果
                    temp_area = temp_area/((scale_h*scale_h)*(scale_w*scale_w))
                    index_area = temp_area > area
                    boxes_keep = temp_boxes[index_area]
                else:
                    index_area = (temp_area < area) & ((max(0.1*area,50.0)< temp_area))#除了删除cropPic预测的大目标外，还要删除过小的目标，这里默认面积是50
                    #如果是cropNum还要按照boxes中心再筛选一次,boxes中心必须在windows之内
                    boxes_center_x = (temp_boxes[:,2]+temp_boxes[:,0])/2
                    boxes_center_y = (temp_boxes[:,3]+temp_boxes[:,1])/2
                    #注意位运算&和>,<,==的优先级，必须用括号
                    index_window = (boxes_center_x>window_x1) & (boxes_center_x<window_x2)&\
                                   (boxes_center_y>window_y1) & (boxes_center_y<window_y2)
                    boxes_keep = temp_boxes[index_area&index_window]
                keep_boxes.append(boxes_keep)
        return keep_boxes
    def mdify_boxes_coor(self,boxes):
        #删除完成后的坐标矫正,还原坐标
        B = self.B
        N = self.B_N//B
        mdify_boxes = []
        for batch in range(B):
            onepic_boxes = []
            scale_h,scale_w,margin_h,margin_w = self.restore_para[batch][-1]
            window_h,window_w = self.restore_para[batch][0][3],self.restore_para[batch][0][2]
            crop_Num = np.sqrt(N-1)
            origin_h,origin_w = window_h*crop_Num,window_w*crop_Num#原图坐标范围
            for cropboxes_index in range(N):
                boxes_idx = batch*N + cropboxes_index
                temp_boxes = boxes[boxes_idx]
                #超出边界的盒子矫正到框内,或者根据预测的情况，矫正这一步不做直接交给YOLOX预测的boxes_regression修正系数-------------------mark
                # temp_boxes[0][temp_boxes[0]<0] = 0
                # temp_boxes[1][temp_boxes[1]<0] = 0
                # temp_boxes[2][temp_boxes[2]>window_w] = window_w
                # temp_boxes[3][temp_boxes[3]>window_h] = window_h
                if cropboxes_index == N-1:
                    temp_boxes = temp_boxes/np.array([scale_w,scale_h,scale_w,scale_h])
                else:
                    x0,y0 = self.restore_para[batch][cropboxes_index][0:2]
                    temp_boxes = temp_boxes + np.array([x0,y0,x0,y0])#加上图片的坐标原点，还原到大图片中
                onepic_boxes.append(temp_boxes)
            onepic_boxes = np.vstack(onepic_boxes)
            onepic_boxes = onepic_boxes - np.array([margin_w,margin_h,margin_w,margin_h])
            #超出边界的调回来
            onepic_boxes[:,0][onepic_boxes[:,0]<0] = 0
            onepic_boxes[:,0][onepic_boxes[:,0]<0] = 0
            onepic_boxes[:,0][onepic_boxes[:,0]>origin_w] = origin_w
            onepic_boxes[:,0][onepic_boxes[:,0]>origin_h] = origin_h
            mdify_boxes.append(onepic_boxes)
        return mdify_boxes

#初始化聚类的类，输入官方数据train.csv路径
Cluster = ObjectSizeCluster("../input/tensorflow-great-barrier-reef/train.csv")
#绘制聚类结果，黑色小点为异常离群点，排除
Cluster.plot_cluster()
#排除异常点后，get_cluster_size获取不同分位置的聚类尺寸，输入参数未n_max,n_min,默认取最大分位置0.4，最小分位值0.05

# h_max,w_max,h_min,w_min = Cluster.get_cluster_size()
Anchors = []
bins = np.linspace(0.1,1.0,10)#这里是最大/最小分位置的列表，取0.1，0.2，0.3，0.4，0.5，其实n_max近似=1-n_min的效果
for i in bins:
    h_max,w_max,h_min,w_min = Cluster.get_cluster_size(i,i)
    Anchors.append([h_max,w_max])
    Anchors.append([h_min,w_min])


print(Anchors)
h_max,w_max,h_min,w_min = 41.83136548693031,47.13912242193276,25.919899316610095,28.88111247934324
h_max,w_max,h_min,w_min = 43.99999749999993,50.0,24.0,26.00000495238237
#计时
# t1  = time.time()
# CropResolutionPic = CropResolutionPic(max_box_size=[h_max,w_max],min_box_size=[h_min,w_min])
# img = cv2.imread("0.jpg")
# img = cv2.imread("1.jpg")
# imgs = np.stack([img,img],axis=-1).transpose(3,0,1,2)
# boxes = [np.array([[250.5,260.5,450.5,450.5],[250.5,260.5,450.5,450.5]]),np.array([[0.,8.,59.,120.]])]
# crop_imgset_batch,boxes_batch,restore_para_batch = CropResolutionPic.get_batch_crop(imgs,boxes)
# t2  = time.time()
# print(t2-t1)
# predicts = []
# for j in range(2):
#     predict = []
#     for i in range(17):
#         box_num = np.random.randint(0,5)
#         if box_num == 0:
#             temp = np.zeros((1,4))
#         else:
#             temp = np.random.randint(0,150,(box_num,4))
#             temp[:,-2:]+=temp[:,:2]
#         predicts.append(temp)
# box_process=RestoreBoxes(boxes=predicts,restore_para = restore_para_batch).boxes



#----------------需要优化，主要耗时的代码是numpy.array转tensor这一行，约1.3秒，可以加载数据上花功夫,直接写成torch版本
# for i in range(len(crop_imgset_batch)):
#     cv2.imshow("i",np.array(crop_imgset_batch[i]))
#     # print(boxes_batch[i])
#     # print(restore_para_batch[i])
#     cv2.waitKey(0)
