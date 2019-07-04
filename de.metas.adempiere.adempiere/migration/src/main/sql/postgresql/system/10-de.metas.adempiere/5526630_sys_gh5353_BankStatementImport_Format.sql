INSERT INTO public.ad_impformat (ad_impformat_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name,
 description, ad_table_id, formattype, processing, ismultiline, ismanualimport) VALUES (1000001, 1000000, 1000000, 
 'Y', '2019-07-03 12:18:03.000000', 2188223, '2019-07-03 12:18:03.000000', 2188223, 'NEW BANK FORMAT', null, 600, 'T', 'N', 'N', 'N');
 
 
 
 
 
INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id,
 seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 
 
 VALUES (1000023, 1000000, 1000000, 'Y', '2019-07-03 12:59:02.000000', 2188223, '2019-07-03 12:59:08.000000', 2188223, 1000001, 
 60, 'trx type', 9323, 5, null, 'S', null, '.', 'N', null, null, null);
 
 
 
INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id, 
seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 

VALUES (1000024, 1000000, 1000000, 'Y', '2019-07-03 12:59:33.000000', 2188223, '2019-07-03 12:59:42.000000', 2188223, 1000001,
 70, 'line description', 9299, 6, null, 'S', null, '.', 'N', null, null, null);
 
 
 
 
INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id,
 seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 
 
 VALUES (1000026, 1000000, 1000000, 'Y', '2019-07-03 13:00:33.000000', 2188223, '2019-07-03 13:00:40.000000', 2188223, 1000001,
 90, 'currency code', 10382, 8, null, 'S', null, '.', 'N', null, null, null);
 
 
 
INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id,
 seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 
 
 VALUES (1000025, 1000000, 1000000, 'Y', '2019-07-03 13:00:10.000000', 2188223, '2019-07-03 16:11:49.000000', 2188223, 1000001,
 80, 'stmt amt', 9318, 7, null, 'N', null, ',', 'N', null, null, null);
 
 
 

INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id, 
seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 

VALUES (1000018, 1000000, 1000000, 'Y', '2019-07-03 12:18:32.000000', 2188223, '2019-07-03 13:08:18.000000', 2188223, 1000001,
 20, 'valutaDate', 9294, 1, null, 'D', 'dd.mm.yyyy', '.', 'N', null, null, null);
 
 
 
INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id, 
seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 
VALUES (1000019, 1000000, 1000000, 'Y', '2019-07-03 12:19:20.000000', 2188223, '2019-07-03 14:00:09.000000', 2188223, 1000001,
 10, 'header IBAN', 568331, null, null, 'C', null, '.', 'N', 'DE97 7007 0010 0202 7399 00', null, null);
 
 
 
INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id, 
seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 

VALUES (1000021, 1000000, 1000000, 'Y', '2019-07-03 12:57:44.000000', 2188223, '2019-07-03 12:58:00.000000', 2188223, 1000001, 
40, 'Partner Name', 568333, 3, null, 'S', null, '.', 'N', null, null, null);





INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id, 
seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 

VALUES (1000022, 1000000, 1000000, 'Y', '2019-07-03 12:58:36.000000', 2188223, '2019-07-03 12:58:45.000000', 2188223, 1000001,
 50, 'iban to', 568332, 4, null, 'S', null, '.', 'N', null, null, null);
 
 
 
INSERT INTO public.ad_impformat_row (ad_impformat_row_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_impformat_id, 
seqno, name, ad_column_id, startno, endno, datatype, dataformat, decimalpoint, divideby100, constantvalue, callout, script) 
VALUES (1000020, 1000000, 1000000, 'Y', '2019-07-03 12:21:37.000000', 2188223, '2019-07-03 13:08:16.000000', 2188223, 1000001,
 30, 'statement line date', 10373, 2, null, 'D', 'dd.mm.yyyy', '.', 'N', null, null, null);



