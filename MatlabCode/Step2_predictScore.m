function Step2_predictScore()
clear;clc;
isSelfEvalute = 0; %1:从标注数据中抽出1/3进行验证，0:直接对测试数据进行标注
selectFeatureSet = 0; %是否选择特征，1 进行选择直到找到最大值(此时isSelfEvalute必须为1)，0 不进行选择
method = 'MLR'; % RF(Random Forest), SVM, MLR(MultiNomial logistic Regression)
isKernel = 0; % 1:SVM是否使用Gaussian高斯核，0: SVM采用线性Linear
hasLoadedData = 1; %0: 还没有加载过数据，1: 已经加载过数据了
needPostProcess = 0; %是否进行后处理
if ~hasLoadedData
    % 先加载特征
    load('./../Data/features/Step01_01_10_train.txt');
    load('./../Data/features/Step01_01_10_test.txt');
    load('./../Data/features/Step01_11_14_train.txt');
    load('./../Data/features/Step01_11_14_test.txt');
    load('./../Data/features/Step01_11_14_train4valid.txt');
    load('./../Data/features/wp_train.txt');    
    load('./../Data/features/wp_test.txt');    
    % 加载标签
    load('./../Data/train/truth_train.csv');
    load('./../Data/sampleSubmission.csv/sampleSubmission.csv');
    %% 标注数据特征选择并融合
    %把时间起始和终止时刻从特征集中剔除
    Step01_01_10_train(:,1:2)=[];
    if ~isSelfEvalute
        Step01_11_14_train(:,3)=[];
        labeled_fea = [Step01_01_10_train, Step01_11_14_train, wp_train ];
    else
        Step01_11_14_train4valid(:,3)=[];
        labeled_fea = [Step01_01_10_train, Step01_11_14_train4valid, wp_train];
    end

    %% 测试数据特征选择并融合
    Step01_01_10_test(:,1:2)=[];
    Step01_11_14_test(:,3)=[];
    unlabeled_fea = [Step01_01_10_test, Step01_11_14_test, wp_test];
    clear Step01_*;
    
    % 读取标签数据
    train_labels = truth_train(:,2);
    clear truth_train;    
    
    save './../Data/features/all_Fea.mat' labeled_fea train_labels unlabeled_fea sampleSubmission  
else
    load('./../Data/features/all_Fea.mat')
end
parameters.method = method;
parameters.isKernel = isKernel;
parameters.isSelfEvalute = isSelfEvalute;
parameters.sampleSubmission = sampleSubmission;
parameters.needPostProcess = needPostProcess;
%% 提出验证集合
iniIndx = 3;
if isSelfEvalute
    % 如果是自己测试的话，则从训练集合中抽出一部分作为测试数据
    unlabeled_fea = labeled_fea(iniIndx:3:120542 ,:);
    test_labels = train_labels(iniIndx:3:120542,:);
    labeled_fea(iniIndx:3:120542 ,:) = [];
    train_labels(iniIndx:3:120542 ,:) = [];
else
    test_labels = zeros(length(unlabeled_fea(:,1)),1);
end

%% 特征全部归一
maxVec = max([labeled_fea;unlabeled_fea]);
labeled_maxMat = repmat(maxVec,length(labeled_fea(:,1)),1);
unlabeled_maxMat = repmat(maxVec,length(unlabeled_fea(:,1)),1);
minVec = min([labeled_fea;unlabeled_fea]);
labeled_minMat = repmat(minVec,length(labeled_fea(:,1)),1);
unlabeled_minMat = repmat(minVec,length(unlabeled_fea(:,1)),1);
labeled_fea = (labeled_fea - labeled_minMat)./(labeled_maxMat-labeled_minMat);
unlabeled_fea = (unlabeled_fea - unlabeled_minMat)./(unlabeled_maxMat-unlabeled_minMat);
clear *min*;
clear *max*;

%% 开始进行特征选择，如果需要的话，不需要的话则直接模型训练
feature_dim = length(labeled_fea(1,:));
disp(['The init feature_dim is:',num2str(feature_dim)]);
best_feature_set = ones(1,feature_dim);
curr_best_feature_index = find(best_feature_set==1);
parameters.curr_best_feature_index = curr_best_feature_index;
if (isSelfEvalute && selectFeatureSet)
    hist_topAUC_score = 0;
    curr_topAUC_score = 0;
    curr_topAUC_ridIndex = 0;
    curr_AUC_score = [];
    %先进行全特征评估
    curr_best_feature_index = find(best_feature_set==1);
    parameters.curr_best_feature_index = curr_best_feature_index;
    [~,hist_topAUC_score] = evaluateMode(labeled_fea, train_labels, unlabeled_fea, test_labels, parameters);
    disp(['The init AUC score is:',num2str(hist_topAUC_score),'!']);
    % 如果特征维数是m,那么最多选择m-1轮选择
    for iter_i=1:(feature_dim-1);
        %开始遍历剩下的特征集合，每次遍历找到一个剔除后得分最高的特征
        %跟上次得分进行对比，如果得分升高了则进行剔除，如果得分下降了直接终止循环
        curr_best_feature_index = find(best_feature_set==1);
        curr_best_feature_set_size = length(curr_best_feature_index);
        disp(['Current feature_dim is:',num2str(curr_best_feature_set_size)]);
        for fea_i=1:curr_best_feature_set_size;
            parameters.curr_best_feature_index = curr_best_feature_index;
            parameters.curr_best_feature_index(fea_i) = [];
            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
           %% 把利用特征计算验证得分做成函数
            [~,curr_AUC_score(fea_i)] = evaluateMode(labeled_fea, train_labels, unlabeled_fea, test_labels, parameters);
        end
        [curr_topAUC_score, curr_topAUC_ridIndex] = max(curr_AUC_score);
        disp(['Current AUC score is:',num2str(curr_topAUC_score),'!']);
        if curr_topAUC_score <= hist_topAUC_score
            disp(['Has find the best feature sets:[',num2str(best_feature_set),'].']);
            disp(['And the best AUC score is:',num2str(hist_topAUC_score),'!']);
            disp('End iterator!');
            break;
        else
            hist_topAUC_score = curr_topAUC_score;
            %剔除噪音特征
            disp(['The ',num2str(iter_i),' th rid index is:',num2str(curr_topAUC_ridIndex),'!']);
            best_feature_set(curr_topAUC_ridIndex)=0;
        end
        curr_AUC_score=[];
    end
else
    %% 不进行特征选择直接计算
    [~,curr_AUC_score] = evaluateMode(labeled_fea, train_labels, unlabeled_fea, test_labels, parameters);
end
end



function [ACC, AUC_score]=evaluateMode(labeled_fea, train_labels, unlabeled_fea, test_labels, parameters)
method = parameters.method;
isKernel = parameters.isKernel;
isSelfEvalute = parameters.isSelfEvalute;
sampleSubmission = parameters.sampleSubmission;
needPostProcess = parameters.needPostProcess;
curr_best_feature_index = parameters.curr_best_feature_index;
labeled_fea = labeled_fea(:,curr_best_feature_index);
unlabeled_fea = unlabeled_fea(:,curr_best_feature_index);
if (strcmp(method,'SVM'))
%% 进行SVM模型训练和预测
    labeled_fea = sparse(labeled_fea);
    unlabeled_fea = sparse(unlabeled_fea);
    tic
    y = zeros(length(unlabeled_fea(:,1)),1);
    if ~isKernel
        disp('start train linear SVM model ...')
        model = train(train_labels, labeled_fea, '-q');
        disp('start predict test data via linear SVM ...')
        [predict_label, accuracy, predict_scores] = predict(y,unlabeled_fea,model);
    else
        disp('start train kernel SVM model ...')
        model = svmtrain(train_labels,labeled_fea,['-q -c 1 -g ',int2str(length(labeled_fea(1,: )))]);
        disp('start predict test data via kernel SVM model ...')
        [predict_label, accuracy, predict_scores] = svmpredict(y,unlabeled_fea,model);
    end
    save SVMLabel.mat predict_label*;
    toc
    if isSelfEvalute
        ACC = length(find(predict_label == test_labels))/length(test_labels)*100;
        AUC_score = scoreAUC(test_labels, predict_label);
        if ~isKernel
            disp(['[SVM-Linear] Accuracy is ',num2str(ACC)])
            disp(['[SVM-Linear] The area under the ROC curve (AUC) is ',num2str(AUC_score)])
        else
            disp(['[SVM-RBF] Accuracy is ',num2str(ACC)])
            disp(['[SVM-RBF] The area under the ROC curve (AUC) is ',num2str(AUC_score)])
        end
    else
       sampleSubmission(:,2) = predict_label;
       save './../predictSVM_label.txt' sampleSubmission -ascii
    end

elseif (strcmp(method,'RF'))
    %% 进行随机森林分类器（Random Forest）训练和预测
    labeled_fea = full(labeled_fea);
    unlabeled_fea = full(unlabeled_fea);
    nTree = 500;
    tic
    disp('start train Random Forest model ...')
    model = TreeBagger(nTree, labeled_fea, train_labels);
    disp('start predict test data via Random Forest model ...')
    [predict_label,predict_scores] = predict(model, unlabeled_fea);
    predict_label = str2num(cell2mat(predict_label));
    predict_label_p = predict_scores(:,1)*0+predict_scores(:,2)*1;
    toc
    if isSelfEvalute
        ACC = length(find(predict_label == test_labels))/length(test_labels)*100;
        disp(['[Random Forest] Accuracy is ',num2str(ACC)])
        AUC_score = scoreAUC(test_labels, predict_label);
        disp(['[Random Forest] The area under the ROC curve (AUC) is ',num2str(AUC_score)])
    else
       sampleSubmission(:,2) = predict_label;
       save './../predictRandomForest_label.txt' sampleSubmission -ascii
    end

    if isSelfEvalute
        ACC = length(find(predict_label_p == test_labels))/length(test_labels)*100;
        disp(['[Random Forest_P] Accuracy is ',num2str(ACC)])
        index = 1;
        for threshold=0.01:0.01:0.05;
            disp(['Current eps is:', num2str(threshold)]);
            predict_label_p_post = predict_label_p;
            predict_label_p_post((1-predict_label_p_post)<threshold)=1;
            predict_label_p_post((predict_label_p_post-0)<threshold)=0;
            AUC_score = scoreAUC(test_labels, predict_label_p_post);
            disp(['[Random Forest_P] The area under the ROC curve (AUC) is ',num2str(AUC_score)])
            evaluteScore(1,index) = threshold;
            evaluteScore(2,index) = AUC_score;
            index = index + 1;
        end
        save ./../evaluteScore_RF.mat evaluteScore;
    else
       sampleSubmission(:,2) = predict_label_p;
       save './../predictRandomForest_labelP.txt' sampleSubmission -ascii
    end

elseif (strcmp(method,'MLR'))
    %% 进行逻辑回归(多项式MultiNomial logistic Regression)训练和预测
    labeled_fea = full(labeled_fea);
    unlabeled_fea = full(unlabeled_fea);
    tic
    disp('start train model ...')
    train_labels = train_labels+1;
    model = mnrfit(labeled_fea, train_labels);
    disp('start predict test data ...')
    predict_scores = mnrval(model, unlabeled_fea);
    train_labels = train_labels -1;
    predict_label_p = predict_scores(:,1)*0+predict_scores(:,2)*1;
    toc
    if isSelfEvalute
        ACC = length(find(predict_label_p == test_labels))/length(test_labels)*100;
        disp(['[MultiNomial LR] Accuracy is ',num2str(ACC)])
                index = 1;
        for threshold=0.001;
            disp(['Current eps is:', num2str(threshold)]);
            predict_label_p_post = predict_label_p;
            predict_label_p_post((1-predict_label_p_post)<threshold)=1;
            predict_label_p_post((predict_label_p_post-0)<threshold)=0;
            AUC_score = scoreAUC(test_labels, predict_label_p_post);
            disp(['[MultiNomial LR] The area under the ROC curve (AUC) is ',num2str(AUC_score)])
            evaluteScore(1,index) = threshold;
            evaluteScore(2,index) = AUC_score;
            index = index + 1;
        end
        save ./../evaluteScore_MLR.mat evaluteScore;
    else
        sampleSubmission(:,2) = predict_label_p;
        save './../predictMultiNomialLR_labelP.txt' sampleSubmission -ascii
    end
end
end

