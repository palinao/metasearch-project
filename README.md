# metasearch-project
Code, runs and evaluation results for the metasearch-project (IR class of MS in Computer Engineering)

Folder contents:

- runs : the 10 original runs generated with Terrier and the related settings files, they are named after the model.
- results : the merged runs obtained with the rank fusion strategies, named after the strategy.
- measures : the trec_eval output for the merged runs, named after the strategy.

---------------------------------------------------------------------------------------------------------------------

The Java code is already compiled, the main class is RankFusion. It is the one that has to be executed. It will output the merged runs in the `results` folder. Note that the execution will overwrite the runs that already exist, which are related to the evaluation results.

The code works by parsing the files in the `runs` folder and it expects to receive 10 runs, in TREC standard format, with the following names:

TD_IDF_0.res, TD_IDF_2.res, TD_IDF_5.res, TD_IDF_8.res, BM25b0.75_1.res, BM25b0.75_4.res, BM25b0.75_6.res, BM25b0.75_9.res, BB2c1.0_3.res, BB2c1.0_7.res

author: Luca Pietrogrande
