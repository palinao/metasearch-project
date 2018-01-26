# metasearch-project
Code, runs and evaluation results for the metasearch-project (IR class of MS in Computer Engineering)

Folder contents:

- runs : the 10 original runs generated with Terrier and the related settings files, named after the model.
- results : the merged runs obtained with the basic strategies, named after the strategy.
- measures : the trec_eval output for the merged runs, named after the strategy.

---------------------------------------------------------------------------------------------------------------------

The Java code is already compiled, the main class is RankFusion. It is the one that has to be executed. It will output the merged runs in "results" folder. Note that the execution will overwrite the runs that already exist, which are related to the evaluation results.

The code works by parsing the files in "runs" folder and is thought to be working with 10 runs, in TREC standard format, with the following names:

TD_IDF_0.res, TD_IDF_2.res, TD_IDF_5.res, TD_IDF_8.res, BM25b0.75_1.res, BM25b0.75_4.res, BM25b0.75_6.res, BM25b0.75_9.res, BB2c1.0_3.res, BB2c1.0_7.res

Different runs have to be named like this.

author: Luca Pietrogrande
