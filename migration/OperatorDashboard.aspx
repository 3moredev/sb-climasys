<%@ Page Title="" Language="C#" MasterPageFile="~/MasterPage/MasterPage.Master" AutoEventWireup="true"
    CodeBehind="OperatorDashboard.aspx.cs" Inherits="ClinicManagementSystemV2.Appointments.OperatorDashboard" %>

<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc3" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc4" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc1" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="Ajax" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="asp" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc5" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc6" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc2" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc14" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="cc8" %>
<asp:Content ID="Content1" ContentPlaceHolderID="head" runat="server">
    <script src="../script/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="../script/jquery.multiselect.js" type="text/javascript"></script>
    <script src="../script/jquery.actual.js" type="text/javascript"></script>
    <script src="../script/jquery.actual.min.js" type="text/javascript"></script>
    <link href="../css/jquery.multiselect.css" rel="stylesheet" type="text/css" />
    <link href="../css/waitMe.css" rel="stylesheet" type="text/css" />
    <link href="../styles/waitMe.min.css" rel="stylesheet" type="text/css" />
    <script src="../script/waitMe.min.js" type="text/javascript"></script>
    <script src="../script/waitMe.js" type="text/javascript"></script>
    <style type="text/css">
        .labelbold01 {
    font-weight: bold;
    font-size: 22px;
    color: #075398;
        
}
        .OD_NN_Label
        {
            color: orange;
        }
        .ms-options-wrap > .ms-options > ul label
        {
            text-align: left !important;
        }
        .nmclsElseProcedure
        {
            margin-left: 109px;
            float: left;
            margin-top: -27px;
        }
        ul.nav.nav-tabs li.active a
        {
            background: #1f68b1;
            color: #fff;
            border: 0px;
        }
        ul.nav.nav-tabs li a
        {
            border: 0px;
            background: none;
            background-color: #eee;
            padding: 10px 20px;
            color: black;
        }
        .nmcls
        {
            float: left;
            margin-top: -7px;
        }
        .nmclsElse
        {
            margin-left: 109px;
            float: left;
            margin-top: -24px;
        }
        .nmcls1
        {
            float: left;
            margin-left: 110px;
            margin-top: -6px;
        }
        .nmclsForBill
        {
            float: left;
            margin-left: 125px;
            margin-top: -6px;
        }
        .nmclsPrescription
        {
            float: left;
            margin-left: 110px;
            margin-top: -6px;
        }
        .tab
        {
            position: absolute;
            margin-left: 112px;
        }
        .popZindex
        {
            z-index: 101 !important;
        }
        .popZindexx
        {
            z-index: 100 !important;
        }
        .popZindexAddDoctorToList
        {
            z-index: 99 !important;
        }
        .txtColor
        {
            color: #075398;
        }
        @media only screen and (max-width: 1350px) and (min-width: 1380px)
        {
            .pop_up_resp
            {
                height: 95%;
                margin-top: 7%;
            }
        }
        
        @media only screen and (max-width: 1275px) and (min-width: 1020px)
        {
            .pop_up_resp
            {
                width: 970px;
                margin-left: -190px;
                overflow-y: scroll;
                max-height: 82%;
                top: 35px;
            }
        }
        
        
        @media only screen and (max-width: 1920px) and (min-width: 1681px)
        {
            .modal-lg-popup
            {
                width: 1256px !important;
                margin-left: -215px !important;
                overflow-y: scroll !important;
                max-height: 90% !important;
            }
        
        }
        @media only screen and (min-width : 1441px) and (max-width : 1680px)
        {
        
            .modal-lg-popup
            {
                width: 1256px !important;
                margin-left: -334px !important;
                overflow-y: scroll !important;
                max-height: 75% !important;
            }
        }
        
        @media only screen and (max-width: 1364px) and (min-width: 1275px)
        {
            .pop_up_resp
            {
                width: 1200px;
                margin-left: -300px;
                overflow-y: scroll;
                max-height: 105%;
                top: -40px;
            }
        }
        
        
        
        @media only screen and (max-width: 1379px) and (min-width: 1365px)
        {
            .pop_up_resp
            {
                width: 1175px;
                margin-left: -290px;
                overflow-y: scroll;
                max-height: 85%; /* 105%;*/
                top: 20px; /*-35px;*/
            }
        }
        
        
        
        
        
        @media only screen and (max-width: 1440px) and (min-width: 1380px)
        {
            .pop_up_resp
            {
                width: 1175px;
                margin-left: -290px;
                overflow-y: scroll;
                max-height: 115%;
                top: -75px;
            }
        }
        
        @media only screen and (max-width: 1680px) and (min-width: 1441px)
        {
            .pop_up_resp
            {
                width: 1256px;
                margin-left: -335px;
                overflow-y: scroll;
                max-height: 105%;
                top: -70px;
            }
        }
        
        
        
        
        .fntsz
        {
            font-size: 11px !important;
        }
        
        table th
        {
            background: #3a6f9f;
            color: #fff;
            text-align: center; /*padding-left: 21px !important;*/
        }
        .Calendar
        {
            padding: 0;
            background: white;
            border: 1px solid #646464;
            width: 177px;
        }
        .NoDisplay
        {
            display: none;
        }
        .Calendar .ajax__calendar_footer
        {
            display: none;
        }
        .Calendar .ajax__calendar_active
        {
            color: #E51736;
            font-weight: bold;
            background-color: #ddd;
            border: 2px solid grey;
            font-size: 12px;
        }
        .Background
        {
            background-color: Black;
            filter: alpha(opacity=90);
            opacity: 0.8;
        }
        .Popup
        {
            width: 600px;
            height: 700px;
        }
        .imgbtn
        {
            height: 25px;
            width: 70px;
            float: right;
            border-width: 0px;
        }
        .lbl
        {
            font-size: 16px;
            font-style: italic;
            font-weight: bold;
        }
        iframe
        {
            width: 800px;
            height: 700px;
        }
        .autocomplete_completionListElement
        {
            margin: 0px !important;
            background-color: inherit;
            color: windowtext;
            border: buttonshadow;
            border-width: 1px;
            border-style: solid;
            cursor: 'default';
            overflow: auto;
            height: 200px;
            font-family: Tahoma;
            font-size: small;
            text-align: left;
            list-style-type: none;
        }
        /* AutoComplete highlighted item */
        .autocomplete_highlightedListItem
        {
            background-color: #ffff99;
            color: black;
            padding: 1px;
        }
        
        /* AutoComplete item */
        .autocomplete_listItem
        {
            background-color: window;
            color: windowtext;
            padding: 1px;
        }
        
        .btn-pop-up
        {
            margin-left: 35%;
        }
        .lbl-lab-test
        {
            margin-top: 7px;
            padding-right: 50px;
            float: left;
        }
        
        .lbl-ddl-form
        {
            margin-top: 30px; /*margin-left: -23px !important;*/
        }
        
        .lbl_patient_folder
        {
            float: left;
        }
        .lbl_habit_allergy
        {
            float: left;
        }
        table > tbody > tr > td > a
        {
            padding: 5px;
        }
        .lbl-folder-adj
        {
            margin-left: 7px;
        }
        .checkboxgrid
        {
            vertical-align: sub !important;
        }
        
        @media (min-width: 768px)
        {
            .operator-modal-dialog-adjs
            {
                width: 850px !important;
                margin: 60px -126px;
            }
        
            .enter-result-modal-adj
            {
                width: 900px;
                margin: 31px -140px;
            }
        }
        
        @media only screen and (min-width: 768px) and (max-width: 768px)
        {
            .operator-modal-dialog-adjs
            {
                width: 733px !important;
            }
        }
        
        @media only screen and (min-width: 800px) and (max-width: 800px)
        {
            .operator-modal-dialog-adjs
            {
                width: 733px !important;
            }
        }
        /**23 March'16 Added**/
        @media only screen and (min-width: 241px) and (max-width: 319px)
        {
            iframe
            {
                width: 240px;
                height: 700px;
            }
            .Popup
            {
                width: 240px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 240px;
                margin: 10px auto;
                top: 128px;
            }
            table td span
            {
                font-size: 11px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                float: initial;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
            }
        }
        @media only screen and (min-width: 320px) and (max-width: 360px)
        {
            iframe
            {
                width: 320px;
                height: 700px;
            }
            .Popup
            {
                width: 320px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 300px;
                margin: 10px auto;
                top: 44px;
            }
            table td span
            {
                font-size: 12px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                float: initial;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
                margin-left: 90px;
            }
            .lbl_habit_allergy
            {
                float: none;
            }
        
        }
        @media only screen and (min-width: 361px) and (max-width: 400px)
        {
        
            iframe
            {
                width: 360px;
                height: 700px;
            }
            .Popup
            {
                width: 360px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 350px;
                margin: 10px auto;
                top: 35px;
            }
            table td span
            {
                font-size: 13px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                float: initial;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
            }
            .lbl_habit_allergy
            {
                float: none;
            }
        
        }
        @media only screen and (min-width: 401px) and (max-width: 480px)
        {
            iframe
            {
                width: 400px;
                height: 700px;
            }
            .Popup
            {
                width: 400px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 380px;
                margin: 10px auto;
                top: 128px;
            }
            table td span
            {
                font-size: 13px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                float: initial;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
            }
            .lbl_habit_allergy
            {
                float: none;
            }
        
        }
        @media only screen and (min-width: 481px) and (max-width: 600px)
        {
            .Popup
            {
                width: 480px;
                height: 700px;
            }
            iframe
            {
                width: 480px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 440px;
                margin: 10px auto;
                top: 128px;
            }
            table td span
            {
                font-size: 13px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                float: initial;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
                margin-left: 160px;
            }
            .lbl_habit_allergy
            {
                float: none;
            }
        
        }
        @media only screen and (min-width: 601px) and (max-width: 700px)
        {
            iframe
            {
                width: 600px;
                height: 700px;
            }
            .Popup
            {
                width: 600px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 550px;
                margin: 10px auto;
                top: 44px;
            }
            table td span
            {
                font-size: 13px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                float: initial;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
                margin-left: 215px;
            }
            .lbl_habit_allergy
            {
                float: none;
            }
        
        }
        @media only screen and (min-width: 701px) and (max-width: 767px)
        {
        
            iframe
            {
                width: 700px;
                height: 700px;
            }
            .Popup
            {
                width: 700px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 600px;
                margin: 10px auto;
                top: 73px;
            }
            table td span
            {
                font-size: 14px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                float: initial;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
            }
        }
        @media only screen and (min-width: 768px) and (max-width: 992px)
        {
            iframe
            {
                width: 768px;
                height: 700px;
            }
            .modal-dialog
            {
                width: 600px;
                margin: 10px auto;
                top: 128px;
            }
            table td span
            {
                font-size: 14px;
            }
            table td
            {
                width: 25%;
            }
            .lbl_patient_folder
            {
                /*float: initial;*/
                margin-left: -147px;
            }
            .btn-pop-up
            {
                margin-left: 0%;
            }
            .lbl-lab-test
            {
            }
            .lbl_habit_allergy
            {
                float: none;
            }
            .lbl-folder-adj
            {
                margin-left: -128px;
            }
            .lbl-patientname-adj
            {
                margin-left: -135px;
            }
        
        }
        @media only screen and (min-width: 1024px) and (max-width: 1024px)
        {
            .modal-lg-popup
            {
                margin: 68px -180px !important;
            }
        }
        
        #ContentPlaceHolder1_chkfollowUp
        {
            vertical-align: sub !important;
        }
        #ContentPlaceHolder1_chk_personin
        {
            vertical-align: sub !important;
        }
        
        #loading
        {
            width: 100%;
            height: 100%;
            top: 50%;
            left: 0;
            position: fixed;
            display: block;
            opacity: 0.7;
            background-color: transparent;
            z-index: 99;
            text-align: center;
        }
        
        #loading-image
        {
            position: absolute;
            top: 100px;
            left: 240px;
            z-index: 100;
        }
    </style>
       <script type="text/javascript">


//           function disbalemutlidropdownlist() {
//               $("#ddlSymptoms").multiselect("disable");
//           }
        </script>
    <script type="text/javascript">



        function validMobileNumber() {

            var MobileNum = $('#ContentPlaceHolder1_txtModalReferralNumber').val();
            if (MobileNum != "") {
                debugger;
                if (MobileNum.length != 10) {
                 
                    $("#ContentPlaceHolder1_AdddoctorErrorMsg").empty();
                    $("#ContentPlaceHolder1_AdddoctorErrorMsg").append("Phone number must be 10 digits.");
                    
                    return false;
                }
            }
        }

        function isNumberKeyyy(event) {
            ResetErrorMessage();
            var numbersAndWhiteSpace = /[0-9]/g;
            var key = String.fromCharCode(event.which);

            if (event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 9 || numbersAndWhiteSpace.test(key)) {
                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                return true;
            }
            else {
                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                $("#ContentPlaceHolder1_lblErrorMsg").append(FR_NUM);
                return false;
            }
        }


        function isNumberKey(event) {
            ResetErrorMessage();
            var numbersAndWhiteSpace = /[0-9]/g;
            var key = String.fromCharCode(event.which);

            if (event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 9 || numbersAndWhiteSpace.test(key)) {
               
                return true;
            }
            else {
             
                return false;
            }
        }




        function LoadingPage(date) {
            debugger;

            var lodaPage = date.text;
            if (lodaPage != "") {
                $('#mainpage').waitMe({
                    effect: 'ios',
                    text: '',
                    bg: 'rgba(255,255,255,0.7)',
                    color: '#000',
                    maxSize: '',
                    source: ''
                });


                //return true;
                setTimeout(function () {
                    $('#mainpage').waitMe('hide');
                }, 2500);
            }
        }


        function Loading() {
            debugger;
            $('#mainpage').waitMe({
                effect: 'ios',
                text: '',
                bg: 'rgba(255,255,255,0.7)',
                color: '#000',
                maxSize: '',
                source: ''
            });


            //return true;
            setTimeout(function () {
                $('#mainpage').waitMe('hide');
            }, 2500);

        }


        function bindattachment(bool) {

            $("#ContentPlaceHolder1_lblErrorPVPopUp").empty();
            $("#ContentPlaceHolder1_lblErrorPVPopUp").append("");
            //$("#ContentPlaceHolder1_lblsuccessmsg").empty();

            var allowedFiles = [".jpeg", ".pdf", ".png", ".jpg", ".docx", ".xlsx", ".xls", ".doc"];
            var fileUpload = $("#ContentPlaceHolder1_PatientfileUploader").get(0);
            var array = ['jpg', 'jpeg', 'pdf', 'png', 'docx', 'xlsx', 'xls', 'doc'];
            var regex = new RegExp("([a-zA-Z0-9\s_\\.\-:])+(" + allowedFiles.join('|') + ")$");
            var flag = true;
            var var_FilePath = $(fileUpload).val();

            if (var_FilePath.length == 0) {
                return true;
            }
            else {

                if ((fileUpload.files.length > 3) && ((fileUpload.files.length != 0))) {

                    $("html, body").animate({
                        scrollTop: 0
                    }, 600);

                    $("#ContentPlaceHolder1_lblErrorPVPopUp").empty();
                    $("#ContentPlaceHolder1_lblErrorPVPopUp").append(INVALID_REMINDERFILE_COUNT);
                    flag = false;

                }

                var fileUpload = $("#ContentPlaceHolder1_PatientfileUploader").get(0);
                var files = fileUpload.files;

                for (var i = 0; i < files.length; i++) {

                    var name = files[i].name
                    var Extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase();

                    if (array.indexOf(Extension) <= -1) {

                        $("html, body").animate({
                            scrollTop: 0
                        }, 600);

                        $("#ContentPlaceHolder1_lblErrorPVPopUp").empty();
                        $("#ContentPlaceHolder1_lblErrorPVPopUp").append(INVALID_ATTACHMENT);
                        flag = false;
                    }

                }
                if (flag == false) {
                    $(fileUpload).val('');
                    return false;
                }

            }

        }




        function OpenWindowReg(objThis) {

            var addbaseurl = window.location.protocol + '//' + window.location.host + '/';


            var idThis = $(objThis).attr('id');


            var cHdn = '#' + idThis.replace('lnkDocumentname', 'hdnDocumentPathReg');

            var openUrl = $(cHdn).val();

            openUrl = openUrl.replace('//', '/');

            openUrl = addbaseurl + openUrl.replace('~/', '');

            window.open(openUrl, '_blank');
        }


        function OpenWindow(objThis) {


            debugger;
            var addbaseurl = window.location.protocol + '//' + window.location.host + '/';
            //console.log('addbaseurl : ' + addbaseurl);

            var idThis = $(objThis).attr('id');


            var cHdn = '#' + idThis.replace('lnkDocumentnametre', 'hdnDocumentPathtre');

            var openUrl = $(cHdn).val();

            //            if (openUrl.substring(0, 2) == '~/') {
            //                openUrl = openUrl.splice(0);
            //                openUrl = openUrl.removeAt(0);
            //                alert('in');
            openUrl = addbaseurl + openUrl.replace('~/', '');

            //            }


            //            ContentPlaceHolder1_grdFilesUploaded_lnkDocumentname_0
            //            'ContentPlaceHolder1_grdFilesUploaded_hdnDocumentPath_0'

            //            var openUrl = $(objThis).attr('openUrl');
            //alert('openUrl : ' + openUrl);
            window.open(openUrl, '_blank');
            BindMultiselect();
        }

        function OpenWindowfileupload(objThis) {

            var addbaseurl = window.location.protocol + '//' + window.location.host + '/';
            //console.log('addbaseurl : ' + addbaseurl);

            var idThis = $(objThis).attr('id');


            var cHdn = '#' + idThis.replace('lnkDocumentnamerefile', 'hdnDocumentPathrefile');

            var openUrl = $(cHdn).val();

            //            if (openUrl.substring(0, 2) == '~/') {
            //                openUrl = openUrl.splice(0);
            //                openUrl = openUrl.removeAt(0);
            //                alert('in');
            openUrl = addbaseurl + openUrl.replace('~/', '');

            //            }


            //            ContentPlaceHolder1_grdFilesUploaded_lnkDocumentname_0
            //            'ContentPlaceHolder1_grdFilesUploaded_hdnDocumentPath_0'

            //            var openUrl = $(objThis).attr('openUrl');
            //alert('openUrl : ' + openUrl);
            window.open(openUrl, '_blank');
        }

        function OpenWindowAudio(objThis) {

            var addbaseurl = window.location.protocol + '//' + window.location.host + '/';
            //console.log('addbaseurl : ' + addbaseurl);

            var idThis = $(objThis).attr('id');


            var cHdn = '#' + idThis.replace('lnkDocumentnameAudio', 'hdnDocumentPathAudio');

            var openUrl = $(cHdn).val();

            //            if (openUrl.substring(0, 2) == '~/') {
            //                openUrl = openUrl.splice(0);
            //                openUrl = openUrl.removeAt(0);
            //                alert('in');
            openUrl = addbaseurl + openUrl.replace('~/', '');

            //            }


            //            ContentPlaceHolder1_grdFilesUploaded_lnkDocumentname_0
            //            'ContentPlaceHolder1_grdFilesUploaded_hdnDocumentPath_0'

            //            var openUrl = $(objThis).attr('openUrl');
            //alert('openUrl : ' + openUrl);
            window.open(openUrl, '_blank');
        }



        var hostName = window.location.host;
        if (location.protocol == "http:")
            var domain = "http://" + hostName + "/";
        else
            var domain = "https://" + hostName + "/";
        var completeDomain = "./";
        // Module Url
        var LoadURL = domain + "Ajax/";

        $(window).load(function () {


            document.getElementById('ContentPlaceHolder1_txtpulse').focus();


            $(document).keyup(function (e) {
                if (e.keyCode == 27) {

                    $find("mpe3").hide();
                    $find("mpe4").hide();
                    $find("ghh").hide();
                    $find("mpe7").hide();

                }
            });


        });


        function validateForm() {

            var val_Dob = $('#ContentPlaceHolder1_txtAppointmentDate').val();
            var Val_Patient = $('#ContentPlaceHolder1_txtsearchpatient').val();
            var Reg_Date = document.getElementById("ContentPlaceHolder1_txtAppointmentDate").value;
            var today = new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()).getTime();
            var currentDate = today;
            var new_Reg_date = RegDate.split("-");
            RegDate = new Date(new_Reg_date[2], new_Reg_date[0] - 1, new_Reg_date[1]).getTime();


            var val = 0;
            if (Val_Patient == "") {
                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                $("#ContentPlaceHolder1_lblErrorMsg").append(BLANK_PATIENT);
                val = 0;
            }
            else if (val_Dob == "") {
                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                $("#ContentPlaceHolder1_lblErrorMsg").append(BLANK_BOOKING_DATE);
                val = 0;
            }
            else if (RegDate >= currentDate) {
                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                val = 1;
            }
            else {
                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                $("#ContentPlaceHolder1_lblErrorMsg").append(INVALID_BOOKING_DATE);
                val = 0;
            }

            if (val == 0) {
                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                return false;
            } else {
                return true;
            }

        }
        function ClientItemSelected(sender, e) {
            $get("<%=txtsearchpatient.ClientID %>").value = e.get_value();
        }

        function HidePopUp() {
            $find("mpe3").hide();
        }


        function HidePopUpPrevious() {

            $find("mpe1").hide();
        }



        function Generate_ReportReceivedData() {


            $("#ContentPlaceHolder1_hdn_cdg_list").val('');
            var str = '';
            // alert(str);

            var table = $("#TableTest tbody");

            table.find('tr').each(function (i) {
                var $tds = $(this).find('td'),
                        labtestname = $tds.eq(0).text(),
                        parametername = $tds.eq(1).text();

                var paramvalue = $(this).closest('tr').find('input').val();

                if (i == 0) {
                    str = labtestname + "@" + parametername + "@" + paramvalue;
                } else {
                    str = str + "*" + labtestname + "@" + parametername + "@" + paramvalue;
                }
            });


            $("#ContentPlaceHolder1_hdn_cdg_list").val(str.trim());
            if (str == "") {

                var seconds = 2;
                setTimeout(function () {
                    $find("mpe3").hide();
                }, seconds * 1000);
                return false;
            }



            var seconds = 2;
            setTimeout(function () {
                $find("mpe3").hide();
            }, seconds * 1000);

        }


        function validMobileNumberRef() {
            debugger;
            var MobileNum = $('#ContentPlaceHolder1_txtDoctorContact').val();
            var ddldct = $('#ContentPlaceHolder1_ddlReferBy').val();
            if (ddldct != "D") {
                if (MobileNum != "") {
                    debugger;
                    if (MobileNum.length != 10) {

                        $("#ContentPlaceHolder1_lblErrorMsg").empty();
                        $("#ContentPlaceHolder1_lblErrorMsg").append("Phone number must be 10 digits.");

                        return false;
                    }
                }
            }
        }

        function HidePatientVisitPopUptimeout() {
            $("#ContentPlaceHolder1_lblErrorReferred").empty();
            var dec_weight = $("#ContentPlaceHolder1_txtweight").val();
            var MobileNum = $('#ContentPlaceHolder1_txtDoctorContact').val();

            var txtdoctordetails = $('#ContentPlaceHolder1_txtdoctordetails').val();
            var ddldct = $('#ContentPlaceHolder1_ddlReferBy').val();

            if (ddldct == "D") {
                if (txtdoctordetails == "") {
                    $("#ContentPlaceHolder1_lblErrorReferred").empty();
                    $("#ContentPlaceHolder1_lblErrorReferred").append("Please select Referral Doctor");

                    return false;
                }
                else {
                    var seconds = 2;
                    setTimeout(function () {
                        $find("mpe7").hide();
                    }, seconds * 30000);

                }
            }

            if (ddldct != "D") {
                if (MobileNum != "") {
                    debugger;
                    if (MobileNum.length != 10) {

                        $("#ContentPlaceHolder1_lblErrorReferred").empty();
                        $("#ContentPlaceHolder1_lblErrorReferred").append("Phone number must be 10 digits.");

                        return false;
                    }
                    else {
                        var seconds = 2;
                        setTimeout(function () {
                            $find("mpe7").hide();
                        }, seconds * 30000);

                    }
                }
            }

          
            if (dec_weight != "") {
                if (parseFloat(dec_weight) > 200) {
                    $("#ContentPlaceHolder1_lblErrorReferred").empty();
                    $("#ContentPlaceHolder1_lblErrorReferred").append(INCORRECT_WEIGHT);
                    return false;
                }
                else {
                    var seconds = 2;
                    setTimeout(function () {
                        $find("mpe7").hide();
                    }, seconds * 30000);

                }
            }
            else {
                var seconds = 2;
                setTimeout(function () {
                    $find("mpe7").hide();
                }, seconds * 30000);
            }


           
         

        }


        function isNumberKeyWeight(event) {

            var numbersAndWhiteSpace = /[0-9 ]/g;
            var key = String.fromCharCode(event.which);

            if (event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 9 || numbersAndWhiteSpace.test(key)) {
                $("#ContentPlaceHolder1_lblErrorPVPopUp").empty();
                $("#ContentPlaceHolder1_lblsuccessPVPopUp").empty();
                return true;
            }
            else {
                $("#ContentPlaceHolder1_lblsuccessPVPopUp").empty();
                $("#ContentPlaceHolder1_lblErrorPVPopUp").empty();
                $("#ContentPlaceHolder1_lblErrorPVPopUp").append(FR_NUM);
                return false;
            }
        }

        function isNumberKeyOnlyWeight(event) {

            var numbersAndWhiteSpace = /[.0-9 ]/g;
            var key = String.fromCharCode(event.which);

            if (event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 9 || numbersAndWhiteSpace.test(key)) {
                $("#ContentPlaceHolder1_lblErrorPVPopUp").empty();
                $("#ContentPlaceHolder1_lblsuccessPVPopUp").empty();
                return true;
            }
            else {
                $("#ContentPlaceHolder1_lblsuccessPVPopUp").empty();
                $("#ContentPlaceHolder1_lblErrorPVPopUp").empty();
                $("#ContentPlaceHolder1_lblErrorPVPopUp").append(FR_NUM);
                return false;
            }
        }

        function validateDeleteAppointment(ele) {
            var isValid = false;
            var s1 = $(ele).attr('id');
            var s = s1.split('_');
            var var_grd_Id = s[s.length - 1];

            var var_labelstatus = "#" + "ContentPlaceHolder1_grd_TodaysVisitData_lblRegistrationNo_" + var_grd_Id;
            var var_statusID = "#" + "ContentPlaceHolder1_grd_TodaysVisitData_hdnStatusId_" + var_grd_Id;

            var register = $(var_labelstatus).text();

            var status_ID = $(var_statusID).val();


            //            if (register.toString() == 'Complete' || register.toString() == 'Waiting for Medicine') {
            if (status_ID.toString() == '4' || status_ID.toString() == '5') {
                //alert("You cannot delete this appointment");
            }
            else {
                if (confirm("Do you want to cancel this appointment") == true) {

                    return true;
                } else {

                    return false;
                }
            }
        }

        function validateDeleteDocument(ele) {
            if (confirm("Are you sure you want to delete this document?") == true) {

                return true;
            } else {

                return false;
            }


        }





        function validateSaveAppointment(ele) {

            var isValid = false;
            var s1 = $(ele).attr('id');
            var n = s1.lastIndexOf('_');
            var res = s1.substring(n + 1, s1.length);
            $("#ContentPlaceHolder1_lblappointmentsuccess").empty();

            var selet_obj = $('#ContentPlaceHolder1_grd_TodaysVisitData_ddlStatus_' + res);
            var selectedText = $(selet_obj).find("option:selected").text();
            var selectedValue = $(selet_obj).find("option:selected").val();
            var txt_Time1 = $("#ContentPlaceHolder1_txtOlAppointment").val();

            var txt_Time = $("#ContentPlaceHolder1_grd_TodaysVisitData_txtOlAppointment_" + res).val();
            //            if (selectedText == 'With Doctor (On Phone)') {
            if (selectedValue == 3) {

                //                if (confirm("Do you want to continue with status With Doctor (On Phone)?") == true) {
                if (confirm("Do you want to continue with status " + selectedText + "?") == true) {

                    isValid = validateHhMm(txt_Time);
                    return isValid;
                }
                else {
                    return false;
                }
            }
            else {
                isValid = validateHhMm(txt_Time);
                return isValid;
            }
        }


        function Valid() {
            var lab = [];
            lab.length = 0;
            $.each($("#ContentPlaceHolder1_ddlLabTest option:selected"), function (key, value) {

                lab.push(value.innerText);


            });
            //$('#TableTest tbody > tr').remove();
            var Lablength = lab.length;
            debugger;
            for (var i = 0; i < Lablength; i++) {
                var var_dropdownselectedvalue = lab[i];
                Addtest(var_dropdownselectedvalue);
            }
            return false;
        }

        function Addtest(id) {


            var var_dropdownselectedvalue = id;
            var url = LoadURL + "GetParameter.aspx";
            //alert(url);
            var var_doctorId = document.getElementById('<%= hdnDoctorId.ClientID%>').value;

            //alert(var_dropdownselectedvalue);
            if ((var_dropdownselectedvalue == "--Select--")) {
                $("#ContentPlaceHolder1_lblTestDuplicateError").empty();
                $("#ContentPlaceHolder1_lblTestDuplicateError").append(SELECT_VAL_ERR);
                return false;
            }

            else {

                $("#ContentPlaceHolder1_lblTestDuplicateError").empty();
                $.ajax({
                    type: 'Post',
                    url: url,
                    data: {
                        p_str_DoctorId: var_doctorId,
                        p_str_LabTestId: id


                    },
                    async: false,
                    success: function (response) {
                        //                    alert(this.url);
                        //                    alert("success :" + response);


                        var data = JSON.parse(response);
                        var length = data.length;
                        var table = document.getElementById("TableTest")
                        var table_len = (table.rows.length) - 1;

                        //alert(table_len);
                        if (length > 0) {

                            for (var i = 0; i < length; i++) {
                                var seen = {};
                                var flag = true;
                                //                                ContentPlaceHolder1_body.innerHTML += "<tr id='row" + table_len + "'><td> " + data[i].Lab_Test_Description + " </td><td> " + data[i].Parameter_Name + "</td><td><input type='text' id='str_Test_Parameter' class='form-control'></input></td><td><span onclick=\"delete_row('" + table_len + "','0','0','0')\" style='Cursor:Pointer'> <i class='fa fa-trash-o' aria-hidden='true'></i></span></td></tr>";

                                $('#TableTest tbody tr').each(function () {

                                    var $tds = $(this).find('td'),
                                    add1 = $tds.eq(0).text(),
                                    add2 = $tds.eq(1).text();


                                    var txt = $(this).text();
                                    var key = $(this).index() + $(this).text();

                                    if (data[i].Lab_Test_Description.trim() == add1.trim() && data[i].Parameter_Name.trim() == add2.trim()) {

                                        flag = false;

                                    }

                                });

                                if (flag) {

                                    //alert('if' + flag);
                                    $('#ContentPlaceHolder1_body').append("<tr id='row" + table_len + "'><td> " + data[i].Lab_Test_Description + " </td><td> " + data[i].Parameter_Name + "</td><td><input type='text' id='str_Test_Parameter' class='form-control' style='font-weight: 200 !important; height:30px !important'></input></td><td style='text-align: center;'><span onclick=\"delete_row('" + table_len + "','0','0','0')\" style='Cursor:Pointer'> <i class='fa fa-trash-o' data-toggle='tooltip' title='Delete' aria-hidden='true'></i></span></td></tr>");
                                    $("#ContentPlaceHolder1_lblTestDuplicateError").empty();

                                }
                                else {

                                    //alert( 'else' + flag);
                                    $("#ContentPlaceHolder1_lblTestError").empty();
                                    $("#ContentPlaceHolder1_lblTestDuplicateError").empty();

                                    document.getElementById("ContentPlaceHolder1_lblTestDuplicateError").innerHTML = 'Selected test is already added';
                                }
                                table_len++;
                            }
                        }
                        else {

                            var flag = true;
                            $('#TableTest tbody tr').each(function () {

                                var $tds = $(this).find('td'),
                                 add1 = $tds.eq(0).text(),
                                 add2 = $tds.eq(1).text();

                                var txt = $(this).text();
                                var key = $(this).index() + $(this).text();



                                if (var_dropdownselectedvalue.trim() == add1.trim() && var_dropdownselectedvalue.trim() == add2.trim()) {

                                    $("#ContentPlaceHolder1_lblTestError").empty();
                                    $("#ContentPlaceHolder1_lblTestDuplicateError").empty();
                                    $("#ContentPlaceHolder1_lblTestDuplicateError").append(SYMPTOMS_VAL_ERR);
                                    flag = false;
                                }

                            });

                            if (flag) {
                                //alert(ContentPlaceHolder1_body.innerHTML);
                                $('#ContentPlaceHolder1_body').append("<tr id='row" + table_len + "'><td> " + var_dropdownselectedvalue + " </td><td>" + var_dropdownselectedvalue + "</td><td><input type='text' id='str_Test_Parameter' class='form-control' style='font-weight: 200 !important; height:30px !important'></input></td><td style='text-align: center;'><span onclick=\"delete_row('" + table_len + "','0','0','0')\" style='Cursor:Pointer; text-align:center'> <i class='fa fa-trash-o' data-toggle='tooltip' title='Delete' aria-hidden='true'></i></span></td></tr>");
                                $("#ContentPlaceHolder1_lblTestDuplicateError").empty();

                            }
                            table_len++;
                        }
                    }
                });


            }
            return false;
        }




        function delete_row(rowid, checkid, var_par, str_parname) {
            //alert(var_par);
            var varConfirm = confirm("Are you sure you want to delete this Test?");
            if (varConfirm == true) {
                $("#ContentPlaceHolder1_lblTestDuplicateError").empty();
                document.getElementById("row" + rowid + "").outerHTML = "";

                if (checkid == 1) {
                    // alert(str_parname);
                    deleteajax(var_par, str_parname);
                }

                else {

                }
            }

        }
        function deleteajax(var_par, str_parname) {
            //   var url = LoadURL + "DeleteParameter.aspx";

            //            var p_str_VisitDate = document.getElementById('<%= hdnVisitDate.ClientID%>').value;

            var p_str_PatientId = document.getElementById('<%= hdnpatientId.ClientID%>').value;
            //var var_doctorId = document.getElementById('<%= hdnDoctorId.ClientID%>').value;
            var p_str_PatientVisitNo = document.getElementById('<%= hdnVisitNo.ClientID%>').value;
            //var p_str_ShiftId = document.getElementById('<%= hdnShiftId.ClientID%>').value;
            var p_str_ClinicId = document.getElementById('<%= hdnClinicId.ClientID%>').value;






            $.ajax({
                type: 'Post',
                url: "http://" + hostName + "/Services/CMSV2Services.asmx/Get_LastTestReports_Details_Delete",
                async: false,
                data: {

                    p_str_Patient_Id: p_str_PatientId,
                    //p_str_Doctor_Id: var_doctorId,
                    p_int_Patient_Visit_No: p_str_PatientVisitNo,
                    //p_int_Shift_Id: p_str_ShiftId,
                    p_str_Clinic_Id: p_str_ClinicId,
                    p_Lab_Test_Description: var_par

                },

                success: function (response) {
                    alter(this.url);
                }




            });
        }

        function HideLabel() {
            var seconds = 5;
            setTimeout(function () {
                $find("mpe3").hide();
            }, seconds * 1000);
        };
    </script>
    <script language="javascript" type="text/javascript">

        function CurrentDateShowing(e) {
            if (!e.get_selectedDate() || !e.get_element().value) {
                e._selectedDate = (new Date()).getDateOnly();
            }
        }


        function BMICalculation() {
            var txtHeight = (document.getElementById("ContentPlaceHolder1_txtHeight").value);
            var txtWeight = (document.getElementById("ContentPlaceHolder1_txtweight").value);
            if (txtHeight != "" && txtWeight != "") {
                (document.getElementById("ContentPlaceHolder1_txtweight").value)
                var Weight = parseFloat(txtWeight);
                var Height = parseFloat(txtHeight);
                var BMI = (Weight / (Height * Height) * 10000);
                document.getElementById("ContentPlaceHolder1_txtBMI").value = Math.round(BMI * 10) / 10;
                return true;
            }
            else {
                document.getElementById("ContentPlaceHolder1_txtBMI").value = "";
            }
            return false;

        }


        function SelectDate() {


            $("#ContentPlaceHolder1_lblErrorMsg").empty();
            $("#ContentPlaceHolder1_lblappointmentsuccess").empty();
            $("#ContentPlaceHolder1_lblErrorRebook").empty();
            $("#ContentPlaceHolder1_lblSuccessRebook").empty();

            var dt_RebookDate = document.getElementById("ContentPlaceHolder1_txtDateOFRebook").value;

            var m_names = new Array("Jan", "Feb", "Mar",
                                            "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                                            "Oct", "Nov", "Dec");

            if (dt_RebookDate != "") {
                var d = new Date();
                var curr_date = d.getDate();
                var curr_month = d.getMonth();
                var curr_year = d.getFullYear();
                currentDate = curr_date + "-" + m_names[curr_month] + "-" + curr_year;


                var todaysdate = parseDMY(currentDate);

                var bookingdate = parseDMY(dt_RebookDate);


                if (bookingdate < todaysdate) {

                    $("#ContentPlaceHolder1_lblErrorRebook").empty();
                    $("#ContentPlaceHolder1_lblErrorRebook").append(PAST_DATE);
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                return false;
            }
        }

        function parseDMY(s) {
            return new Date(s.replace(/^(\d+)\W+(\w+)\W+/, '$2 $1 '));
        }

        function validatedate(value) {

            var dt_currVal = document.getElementById("ContentPlaceHolder1_txtDateOFRebook").value;

            var rxDatePattern = /^(\d{1,2})(\-)([a-zA-Z]{3})(\-)(\d{4})$/;

            var dtArray = dt_currVal.match(rxDatePattern);


            var dtDay = parseInt(dtArray[1]);
            var dtMonth = dtArray[3];
            var dtYear = parseInt(dtArray[5]);

            switch (dtMonth.toLowerCase()) {
                case 'jan':
                    dtMonth = '01';
                    break;
                case 'feb':
                    dtMonth = '02';
                    break;
                case 'mar':
                    dtMonth = '03';
                    break;
                case 'apr':
                    dtMonth = '04';
                    break;
                case 'may':
                    dtMonth = '05';
                    break;
                case 'jun':
                    dtMonth = '06';
                    break;
                case 'jul':
                    dtMonth = '07';
                    break;
                case 'aug':
                    dtMonth = '08';
                    break;
                case 'sep':
                    dtMonth = '09';
                    break;
                case 'oct':
                    dtMonth = '10';
                    break;
                case 'nov':
                    dtMonth = '11';
                    break;
                case 'dec':
                    dtMonth = '12';
                    break;
            }

            // convert date to number
            dtMonth = parseInt(dtMonth);
            var today = new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()).getTime();
            var currentDate = today;

            var m_names = new Array("Jan", "Feb", "Mar",
                                            "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                                            "Oct", "Nov", "Dec");

            var d = new Date();
            var curr_date = d.getDate();
            var curr_month = d.getMonth();
            var curr_year = d.getFullYear();
            currentDate = curr_date + "-" + m_names[curr_month] + "-" + curr_year;
            //alert(Reg_Date);
            var date1 = parseDMY(currentDate);
            // var date2 = parseDMY(Reg_Date);

            if (isNaN(dtMonth)) {
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(FR_DOB_PAT);

                return false;
            }
            else if (dtMonth < 1 || dtMonth > 12) {
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(FR_DOB_PAT);
                return false;
            }
            else if (dtDay < 1 || dtDay > 31) {
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(FR_DOB_PAT);
                return false;
            }
            else if ((dtMonth == 4 || dtMonth == 6 || dtMonth == 9 || dtMonth == 11) && dtDay == 31) {
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(FR_DOB_PAT);
                return false;
            }
            else if (dtMonth == 2) {
                var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
                if (dtDay > 29 || (dtDay == 29 && !isleap)) {
                    $("#ContentPlaceHolder1_lblErrorRebook").empty();
                    $("#ContentPlaceHolder1_lblErrorRebook").append(FR_DOB_PAT);
                    return false;
                }
            }

            $("#ContentPlaceHolder1_lbldateerror").empty();
            $("#ContentPlaceHolder1_lblErrorRebook").empty();
            $("#ContentPlaceHolder1_lblSuccessRebook").empty();

            return true;
        }

        function ResetErrorMessage() {
            $("#ContentPlaceHolder1_lblErrorMsg").empty();
            $("#ContentPlaceHolder1_lblappointmentsuccess").empty();
            $("#ContentPlaceHolder1_lblErrorRebook").empty();
            $("#ContentPlaceHolder1_lblSuccessRebook").empty();
        }

        function CheckCharacterMedicine(textBox, maxLength) {

            if (textBox.value.length > maxLength) {
                textBox.value = textBox.value.substr(0, maxLength);
            }
            else {

            }
        }
    </script>
    <script type="text/javascript">
        function onlybackbutton(id) {

            if (id == 1) {
                document.getElementById('<%=Icon_Calendar.ClientID%>').click();
            }

            return false;
        }



        function validateBookingform() {

            var rxDatePattern = /^(\d{1,2})(\-)([a-zA-Z]{3})(\-)(\d{4})$/

            // var dt_RebookDate = document.getElementById("ContentPlaceHolder1_txtDateOFRebook").value;
            var dt_RebookDate = $("#txtDateOFRebook").val();
            var dt_AdmissionTime_Hours = $("#ContentPlaceHolder1_ddlBookingHours option:selected").text();
            var dt_AdmissionTime_Min = $("#ContentPlaceHolder1_ddlBookingMinutes option:selected").text();

            var dt_AdmissionTimefrom_Hours = $("#ContentPlaceHolder1_ddlBookingHoursto option:selected").text();
            var dt_AdmissionTimefrom_Min = $("#ContentPlaceHolder1_ddlBookingMinutesto option:selected").text();

            /******************************************************/
            var m_names = new Array("Jan", "Feb", "Mar",
                                            "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                                            "Oct", "Nov", "Dec");


            var d = new Date();
            var curr_date = d.getDate();
            var curr_month = d.getMonth();
            var curr_year = d.getFullYear();
            currentDate = curr_date + "-" + m_names[curr_month] + "-" + curr_year;






            /******************************************************/


            var time_rejex = /^(10|11|12|01|02|03|04|05|06|07|08|09|[1-9]):[0-5][0-9]$/;

            if (dt_RebookDate == '') {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(EMPTY_BOOKING_DATE);
                return false;

            }
            else if (dt_RebookDate != "" && !dt_RebookDate.match(rxDatePattern)) {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(FR_DOB_PAT);
                return false;
            }

            var todaysdate = parseDMY(currentDate);
            var Rebookdate = parseDMY(dt_RebookDate);

            if (Rebookdate < todaysdate) {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(PAST_DATE);
                return false;
            }
            else if (Rebookdate == todaysdate) {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(PAST_DATE);
                return false;
            }

            else if (dt_AdmissionTime_Hours == "HH") {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(EMPTY_BOOKING_TIME);

                return false;

            }
            else if (dt_AdmissionTime_Min == "MM") {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(EMPTY_BOOKING_TIME);

                return false;

            }


            else if (dt_AdmissionTimefrom_Hours == "HH") {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(EMPTY_BOOKING_TIME);

                return false;

            }
            else if (dt_AdmissionTimefrom_Min == "MM") {

                $("html, body").animate({
                    scrollTop: 0
                }, 600);
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").append(EMPTY_BOOKING_TIME);

                return false;

            }


            else {


                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                $("#ContentPlaceHolder1_lblErrorRebook").empty();
                $("#ContentPlaceHolder1_lblSuccessRebook").empty();


            }



        }
    </script>
    <script type="text/javascript">

        $(document).ready(function () {
        BindMultiselect();
        });

        function ShowFamilyMedical() {
    $("#Family").addClass('active');
    $("#contact_just").addClass('in');
     $("#Personal").removeClass('active');  
     $("#home_just").removeClass('in');
     $("#Medical").removeClass('active');
    $("#profile_just").removeClass('in'); 
//    $('ul.nav.nav-tabs li.active a').css("background","#075398");
//    $('ul.nav.nav-tabs a').attr("disabled","true");
//     

    $("#myTabContentJust").css("display" , "none");
    $("#contact_just").css("display" , "block");
    $("#profile_just").css("display" , "none");
    

    }

    function ShowPersonalDetail()
    {
     $("#Family").removeClass('active');
    $("#contact_just").removeClass('in');
     $("#Personal").addClass('active');  
     $("#home_just").addClass('in');
     $("#Medical").removeClass('active');
    $("#profile_just").removeClass('in');

//     $('ul.nav.nav-tabs li.active a').css("background","#075398");
//    $('ul.nav.nav-tabs li a').attr("disabled","true");
   


      $("#myTabContentJust").css("display" , "block");
    $("#contact_just").css("display" , "none");
    $("#profile_just").css("display" , "none");

   
    }
    function ShowPersonalMedical()
    { 
       $("#Family").removeClass('active');
    $("#contact_just").removeClass('in');
     $("#Personal").removeClass('active');  
     $("#home_just").removeClass('in');
     $("#Medical").addClass('active');
    $("#profile_just").addClass('in');

//      $('ul.nav.nav-tabs li.active a').css("background","#075398");
//    $('ul.nav.nav-tabs li a').attr("disabled","true");

        $("#myTabContentJust").css("display" , "none");
    $("#contact_just").css("display" , "none");
    $("#profile_just").css("display" , "block");


    }



        function BindMultiselect() {
      
         $('[id*=ddlLabTest]').multiselect({
                columns: 2,
                search: true,
                showOptGroups: true,
                 texts: {
           
            search:          'Add lab test',   
            },
                placeholder: 'Select lab Test'

            });

         $('[id*=ddlSymptoms]').multiselect({
                columns: 3,
                search: true,
                showOptGroups: true,
                 texts: {
           
            search:          'Search Complaints',   
            },
                placeholder: 'Select Complaints'

            });

           
              $('.ms-options-wrap button').click(function(){
               
                $(this).parent().children(':eq(1)').children(':eq(0)').children().focus();
            }); 
        }
    </script>
    <script type="text/javascript">

        function CallConfirmBox() {

            if (confirm("There exist appointment near about given time. Do you still want to continue?") == true) {

                Validate_AppointmentBooking_Time();
                //   $("#btnBokkConf").click();

                // alert("hi1");

                document.getElementById('<%= btnBokkConf.ClientID %>').click();

            } else {

                // alert("hi2");

                document.getElementById('<%= btncloseconfr.ClientID %>').click();

                $find("Re4P").show();

                return false;

            }
        }

        /*
        *  Method Name - Validate_AppointmentBooking_Time 
        *  Created By  - Varsha Khandre
        *  Created On  - 25 May 2017
        *  Modified By - 
        *  Modified On - 
        *  Purpose     - This function is used to shoe treatment plans on button click.
        */

        function Validate_AppointmentBooking_Time() {


            var values = document.getElementById('ContentPlaceHolder1_hdn_Validatetime_Params').value;
            var value_medicine = values.split(',');

            var hostName = window.location.host;

            $.ajax({
                url: "http://" + hostName + "/Services/CMSV2Services.asmx/Book_Patient_Appointment_FromDoctor_Duplicate",
                type: "POST",
                data: {

                    p_str_Visit_Date: value_medicine[0],
                    p_int_Shift_ID: value_medicine[1],
                    p_str_Clinin_ID: value_medicine[2],
                    p_str_Doctor_ID: value_medicine[3],
                    p_str_Patient_ID: value_medicine[4],
                    p_str_Visit_Time: value_medicine[5],
                    p_bool_Report_Received: value_medicine[6],
                    p_str_UserId: value_medicine[7],
                    p_bool_InPerson: value_medicine[8],
                    p_str_ScheduleDay: value_medicine[9],
                    p_int_visitno: value_medicine[10],
                    Current_shiftID: value_medicine[11],
                    p_time_Visit_Time_to: value_medicine[12]
                },
                async: false,
                success: function (response) {

                    var data = response;

                    window.location = "OperatorDashboard.aspx";

                    for (var i = 0; i < data.length; i++) {

                        return true;

                    }

                },
                error: function (jqXHR, exception) {
                    //  alert('error');
                    var msg = '';
                    if (jqXHR.status === 0) {
                        msg = 'Not connect.\n Verify Network.';
                    } else if (jqXHR.status == 404) {
                        msg = 'Requested page not found. [404]';
                    } else if (jqXHR.status == 500) {
                        msg = 'Internal Server Error [500].';
                    } else if (exception === 'parsererror') {
                        msg = 'Requested JSON parse failed.';
                    } else if (exception === 'timeout') {
                        msg = 'Time out error.';
                    } else if (exception === 'abort') {
                        msg = 'Ajax request aborted.';
                    } else {
                        msg = 'Uncaught Error.\n' + jqXHR.responseText;
                    }
                    console.log(msg);
                }
            });
        }
    </script>
    <script type="text/javascript">
        function validateHhMm(inputField) {
            debugger;
            if (inputField == " " || inputField == "") {
                return true;
            }
            var isValid = /^([0-1]?[0-9]|2[0-4]):([0-5][0-9])(:[0-5][0-9])?$/.test(inputField);  //Actual regex which will validate 

            if (isValid) {
                return true;
            }
            else {


                $("#ContentPlaceHolder1_lblErrorMsgForTime").empty();
                $("#ContentPlaceHolder1_lblErrorMsgForTime").append("Invalid time format");

                return false;
            }
            return true;
        }

        //Added By Neha
        //        function isNumberAmount(event, e) {
        //            debugger;
        //            var numbersAndWhiteSpace = /^-?\d*\.?\d*$/;
        //            var key = String.fromCharCode(event.which || event.keyCode);
        //            var charCode = (event.keyCode ? event.keyCode : event.which);
        //            var txt_amount = $("#ContentPlaceHolder1_txtOlAppointment").val();


        //            if (charCode == 9 || charCode == 8 || charCode == 37 || charCode == 39 || numbersAndWhiteSpace.test(key)) {

        //                if (charCode == 8) { return true; }
        //                if (charCode == 9) { return true; }
        //                if (charCode == 32) { return true; }

        //                $("#ContentPlaceHolder1_lblErrorMessage").empty();
        //                return true;
        //            }
        //            else if (charCode == 13) {
        //                event.preventDefault();
        //                return false;
        //            }

        //            else {

        //                $("#ContentPlaceHolder1_lblErrorMsg").empty();
        //                $("#ContentPlaceHolder1_lblErrorMsg").append(PP_NONTEXT_MEDICINE_DAYS);
        //                $("html, body").animate({
        //                    scrollTop: 0
        //                }, 600);
        //                return false;
        //            }
        //      }
    </script>
    <script type="text/javascript">
        function DoctorItemSelected(sender, e) {

            $get("<%=txtdoctordetails.ClientID %>").value = e.get_value();

            $("#ContentPlaceHolder1_Hdnbtndata").click();



        }
        function validdoctor() {
            debugger;
            var ddlreferby = $("#ContentPlaceHolder1_ddlReferBy option:selected").text();

            if (ddlreferby != "Doctor") {
                $("#ContentPlaceHolder1_lblErrorMsg").empty();
                $("#ContentPlaceHolder1_lblErrorMsg").append('Please select the doctor from referred by');
                val = 0;
            }

        }
    </script>
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="ContentPlaceHolder1" runat="server">
    <asp:Button ID="Hdnbtndata" class="btn btn-primary" runat="server" Style="display: none"
        Text="Submit" OnClick="Hdnbtndata_Click" />
    <asp:HiddenField ID="Pant_id" runat="server" />
    <asp:HiddenField ID="HDNBilledAmount" runat="server" />
    <asp:HiddenField ID="HDNComplaint" runat="server" />
    <div id="title-breadcrumb-option-demo" class="page-title-breadcrumb">
        <div class="row">
            <div class="page-header Page_Title_Align">
                <div class="col-xs-12 col-sm-12 col-md-3 col-lg-3">
                    <div class="page-heading">
                        Today's Appointments
                    </div>
                </div>
             <div class="col-xs-12 col-sm-12 col-md-9 col-lg-9" style="float: initial;">
                    <asp:UpdatePanel ID="UpdatePanel69" runat="server" UpdateMode="Conditional">
                        <ContentTemplate>
                            <span class="errorBox">
                                <asp:Label ID="lblErrorMsg" runat="server"></asp:Label>
                                <asp:Label ID="lblErrorMsgForTime" runat="server"></asp:Label>
                                <asp:Label ID="lblappointmentsuccess" runat="server" CssClass="success-message-box" Text=""></asp:Label>
                            </span>
                        </ContentTemplate>
                    </asp:UpdatePanel>
                </div>
                 <div class="col-xs-12 col-sm-12 col-md-1 col-lg-1">
                    <ol class="breadcrumb page-breadcrumb pull-right">
                        <%-- <li><i class="fa fa-home"></i>&nbsp;<asp:HyperLink ID="HyperLinkPR" NavigateUrl="~/Appointments/OperatorDashboard.aspx"
                            runat="server">Home</asp:HyperLink>&nbsp;&nbsp;<i class="fa fa-angle-right"></i>&nbsp;&nbsp;
                        </li>--%>
                        <li><i class="fa fa-home"></i>&nbsp;<asp:Label ID="hyperlbl" runat="server">Home</asp:Label>&nbsp;&nbsp;<i
                            class="fa fa-angle-right"></i>&nbsp;&nbsp; </li>
                        <li class="hidden">
                            <asp:HyperLink ID="HyperLink1" NavigateUrl="#" runat="server">Today's Appointments</asp:HyperLink>&nbsp;&nbsp;<i
                                class="fa fa-angle-right"></i>&nbsp;&nbsp; </li>
                        <li class="active">Today's Appointments</li>
                    </ol>
                </div>
            </div>
        </div>
    </div>
    <!--END TITLE & BREADCRUMB PAGE-->
    <!--BEGIN CONTENT-->
    <div class="page-content" id="mainpage">
        <div class="col-md-12">
            <asp:HiddenField ID="hdnpayment" runat="server" />
            <div class="row" runat="server" id="appointment_div">
                <div class="col-md-6 text-box-adjs">
                    <div class="input-icon right">
                        <i class="fa fa-search"></i>
                        <asp:TextBox ID="txtsearchpatient" runat="server" autocomplete="on" placeholder="Search with Patient ID / Patient Name / Contact Number"
                            class="form-control" onkeypress="return ResetErrorMessage();"></asp:TextBox>
                        <asp:AutoCompleteExtender ID="txtsearchpatient_AutocompleteExtender" runat="server"
                            Enabled="true" TargetControlID="txtsearchpatient" ServiceMethod="SearchPatientDetails"
                            ServicePath="~/Services/CMSV2Services.asmx" OnClientItemSelected="ClientItemSelected"
                            MinimumPrefixLength="2">
                        </asp:AutoCompleteExtender>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="input-icon right">
                        <%-- <asp:TextBox ID="txtAppointmentDate" runat="server" ReadOnly placeholder="MM/dd/YYYY"
                            class="form-control"></asp:TextBox>--%>
                        <asp:DropDownList ID="ddldoctorname" runat="server" class="form-control" onchange="ResetErrorMessage();">
                        </asp:DropDownList>
                    </div>
                </div>
                <div class="col-md-2">
                    <asp:Button ID="btnbookapointment" runat="server" Text="Book Appointment" class="btn btn-primary"
                        OnClick="btnbookapointment_Click" />
                    <asp:Button ID="btnSearch" runat="server" Text="Search" OnClick="btnSearch_Click"
                        class="btn btn-primary" />
                </div>
                <div class="col-md-1 adjustddl" style="margin-left: -17px;">
                    <asp:Button ID="btnNewpatient" runat="server" Text="New Patient" OnClick="btnNewPatient_Click"
                        class="btn btn-primary" Style="margin-left: -35px;" />
                </div>
                <asp:UpdatePanel ID="UpdatePanel50" runat="server" UpdateMode="Conditional">
                    <ContentTemplate>
                        <div class="col-md-1">
                            <asp:Label ID="lblNNCount" CssClass="OD_NN_Label" runat="server"></asp:Label>
                            <asp:Label ID="Label4" CssClass="OD_MM_Label" runat="server" Text="/"></asp:Label>
                            <asp:Label ID="lblMMCount" CssClass="OD_MM_Label" runat="server"></asp:Label>
                        </div>
                    </ContentTemplate>
                </asp:UpdatePanel>
            </div>
            <br />
            <div id="dialog" style="display: none" align="center">
                Do you want to cancel this appointment?
            </div>
            <div class="panel-body">
                <div class="table-responsive">
                    <asp:UpdatePanel ID="UpdatePanel8" runat="server">
                        <ContentTemplate>
                            <asp:Timer ID="GridTimer" runat="server" OnTick="GridTimer_Tick">
                            </asp:Timer>
                            <asp:GridView ID="grd_TodaysVisitData" runat="server" AutoGenerateColumns="false"
                                ShowHeaderWhenEmpty="true" OnRowCommand="grd_CompounderVisitData_RowCommand"
                                Width="100%" Height="60px" CellPadding="4" ForeColor="#333333" AllowPaging="True"
                                PageSize="20" OnRowDataBound="TodaysVisitData_rowdatabound" OnPageIndexChanging="grdAccessModule_PageIndexChanging">
                                <AlternatingRowStyle BackColor="White" />
                                <Columns>
                                    <asp:TemplateField HeaderText="Sr." ItemStyle-Width="20px" HeaderStyle-CssClass="cls_right_aligned"
                                        ControlStyle-CssClass="cls_right_float label1">
                                        <ItemTemplate>
                                            <asp:Label ID="lblSrNo" CssClass="label1 fntsz" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                            <asp:HiddenField ID="lblPatientId" Value='<%# Bind("Patient_ID") %>' runat="server">
                                            </asp:HiddenField>
                                            <asp:HiddenField ID="lblvisitdate" runat="server" Value='<%# Bind("Visit_Date") %>'>
                                            </asp:HiddenField>
                                            <asp:HiddenField ID="lblPatientVisitNo" Value='<%# Bind("Patient_Visit_No") %>' runat="server">
                                            </asp:HiddenField>
                                            <asp:HiddenField ID="hdnStatusId" runat="server" Value='<%# Bind("Status_ID") %>'>
                                            </asp:HiddenField>
                                            <asp:HiddenField ID="hdnCurrentVisitNo" Value='<%# Bind("Patient_Visit_No") %>' runat="server" />
                                            <asp:HiddenField ID="hdnconfirmdelete" runat="server"></asp:HiddenField>
                                            <asp:HiddenField ID="hdnIconColorChange" runat="server" Value='<%# Bind("Is_Submit_Patient_Visit_Details") %>'>
                                            </asp:HiddenField>
                                            <asp:HiddenField ID="hdn_Icon_change_Labtest" runat="server" Value='<%# Bind("Is_Submit_Patient_LabTest") %>'>
                                            </asp:HiddenField>
                                            <asp:HiddenField ID="hdnDoctorID" runat="server" Value='<%# Bind("Doctor_ID") %>'>
                                            </asp:HiddenField>
                                            <asp:HiddenField ID="hdn_visit_time_" runat="server" Value='<%# Bind("Visit_Time") %>' />
                                            <asp:HiddenField ID="hdn_Visit_Date_Time" Value='<%# Bind("prevLAST_VISIT_DATE") %>'
                                                runat="server" />
                                            <asp:HiddenField ID="hdn_prevDoctor_ID" Value='<%# Bind("prevDoctor_ID") %>' runat="server" />
                                            <asp:HiddenField ID="hdn_Clinic_ID" Value='<%# Bind("Clinic_ID") %>' runat="server" />
                                            <asp:HiddenField ID="hdn_Name" Value='<%# Bind("Name") %>' runat="server" />
                                            <asp:HiddenField ID="hdn_Age" Value='<%# Bind("AgeYearsIntRound") %>' runat="server" />
                                            <asp:HiddenField ID="hdn_Gender" Value='<%# Bind("prevGender_ID") %>' runat="server" />
                                            <asp:HiddenField ID="hdn_doct_specility" Value='<%# Bind("Doctorspecility") %>' runat="server" />
                                            <asp:HiddenField ID="hdn_followupdate" Value='<%# Bind("Follow_Up_Date") %>' runat="server" />
                                            <asp:HiddenField ID="hdn_followup_type" Value='<%# Bind("FollowUp_Description") %>'
                                                runat="server" />
                                            <%--                   <asp:HiddenField ID="hdn_visit_datetime" Value='<%# Bind("Visit_DateTime") %>' runat="server" />--%>
                                            <%-- <asp:HiddenField ID="hdn_Visit_Type" Value='<%# Bind("Visit_type") %>' runat="server" />--%>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <asp:TemplateField HeaderText="Patient Name" ItemStyle-Width="402px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 fntsz">
                                        <ItemTemplate>
                                            <asp:LinkButton ID="lnkName" CssClass="label1 fntsz" Style="text-decoration: underline"
                                                CommandName="export" Text='<%# Bind("Name") %>' runat="server" OnClick="lnkPatientName_Click"></asp:LinkButton>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <asp:TemplateField HeaderText="Age" ItemStyle-Width="10px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1 fntsz">
                                        <ItemTemplate>
                                            <%-- <asp:LinkButton ID="lnkFolder" CssClass="label1" Style="text-decoration: underline"
                                        CommandName="export" Text='<%# Bind("AgeYearsIntRound") %>' runat="server" OnClick="lnkPatientFolder_Click"></asp:LinkButton>--%>
                                            <asp:Label ID="lblAge" CssClass="label1 fntsz" runat="server" Text='<%# Bind("AgeYearsIntRound") %>'></asp:Label>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <asp:TemplateField HeaderText="Contact" ItemStyle-Width="100px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_right_float_contact label1 fntsz">
                                        <ItemTemplate>
                                            <asp:Label ID="lblMobileNumber" CssClass="label1 fntsz" runat="server" Text='<%# Bind("Mobile_1") %>'></asp:Label>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <asp:TemplateField HeaderText="Time" ItemStyle-Width="100px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1 fntsz">
                                        <ItemTemplate>
                                            <asp:Label ID="lblVisitTime" CssClass="label1 fntsz" runat="server" Text='<%# Bind("Visit_Time") %>'></asp:Label>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <%--
                                    <asp:TemplateField HeaderText="In Person" ItemStyle-Width="100px" ItemStyle-VerticalAlign="Middle">
                                        <ItemTemplate>
                                            <asp:CheckBox runat="server" ID="mmf" Checked='<%# Convert.ToBoolean(Eval("In_Person")) ? true : false %>' />
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    --%>
                                    <asp:TemplateField HeaderText="Provider" ItemStyle-Width="160px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 fntsz">
                                        <ItemTemplate>
                                            <%--  <asp:Label ID="lblprovider" CssClass="label1" runat="server" Text='<%# Bind("DoctorName") %>'></asp:Label>--%>
                                            <asp:DropDownList ID="ddlMdoctor" Style="width: 100%; height: 30px; font-weight: normal;"
                                                CssClass="gridcontrolsize fntsz" runat="server" AutoPostBack="True" OnSelectedIndexChanged="ddlOutlet_SelectedIndexChanged">
                                            </asp:DropDownList>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <asp:TemplateField HeaderText="F/Up" ItemStyle-Width="152px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 fntsz"
                                        Visible="false">
                                        <ItemTemplate>
                                            <asp:Label ID="lblFollowup" CssClass="label1 fntsz" runat="server" Text='<%# Bind("FollowUp_Description") %>'></asp:Label>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <asp:TemplateField HeaderText="Online (17:30) Appointment" ItemStyle-Width="152px"
                                        ItemStyle-VerticalAlign="Middle" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 fntsz">
                                        <ItemTemplate>
                                            <asp:TextBox runat="server" ID="txtOlAppointment" class="form-control" Width="100%"
                                                Text='<%# Bind("Online_Appointment_Time") %>' PlaceHolder="HH:MM" BorderStyle="None"
                                                Style="height: 28px;"></asp:TextBox>
                                        </ItemTemplate>
                                        <%--onchange="validateHhMm(this);" OnTextChanged="txtOlAppointment_TextChanged"--%>
                                        <%--OnTextChanged="txtOlAppointment_TextChanged" onchange="return validateHhMm(this);"--%>
                                    </asp:TemplateField>
                                    <%--onkeypress="return isNumberAmount(event,this);"--%>
                                    <asp:TemplateField HeaderText="Status" ItemStyle-Width="318px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 fntsz">
                                        <ItemTemplate>
                                            <asp:Label ID="lblRegistrationNo" CssClass="label1 fntsz" runat="server" Text='<%# Bind("Status_Description") %>'></asp:Label>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <asp:TemplateField HeaderText="Change Status" ItemStyle-Width="265px" ItemStyle-VerticalAlign="Middle"
                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 fntsz">
                                        <ItemTemplate>
                                            <asp:DropDownList ID="ddlStatus" Style="width: 100%; height: 30px; font-weight: normal;"
                                                class="gridcontrolsize" runat="server">
                                            </asp:DropDownList>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <%--  /// added by amol on 28 feb--%>
                                    <asp:TemplateField HeaderText="Last OPD Visit" ItemStyle-VerticalAlign="Middle" ItemStyle-Width="510px"
                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label13 fntsz">
                                        <ItemTemplate>
                                            <%--<asp:Label ID="lblprevDate" CssClass="label1" runat="server" Text='<%# Bind("PreVisitDetails") %>'></asp:Label>--%>
                                            <%--<asp:HyperLink ID="lblprevDate"  CssClass="label1" runat="server" Text='<%# Bind("PreVisitDetails") %>'></asp:HyperLink>--%>
                                            <asp:LinkButton ID="lblprevDate" CssClass="label1 fntsz" Style="width: 100%;" runat="server"
                                                CommandName="LastVisit" OnClientClick="return LoadingPage(this);" Text='<%# Bind("PreVisitDetails") %>'
                                                CommandArgument='<%# Eval("prevLAST_VISIT_DATE","{0:dd-MMM-yyyy}") + "!" + Eval("PrevShift_ID")  + "!" +Eval("PrevPatient_Visit_No") %>'> </asp:LinkButton>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <%--   /// added by amol on 28 feb end--%>
                                    <asp:TemplateField HeaderText="Labs" ItemStyle-Width="100px">
                                        <ItemTemplate>
                                            <asp:CheckBox runat="server" ID="chkReportsAsked" CssClass="checkboxgrid" Checked='<%# Convert.ToBoolean(Eval("Reports_Asked")) ? true : false %>' />
                                            &nbsp;
                                            <asp:LinkButton ID="lnkReportReceive" CssClass="label1 fntsz" Style="width: 100%"
                                                runat="server" OnClick="Check_Clicked"> 
                                    <i class="fa fa-plus-square"  aria-hidden="true"></i></asp:LinkButton>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                    <%--             <asp:TemplateField HeaderText="Visit Details" ItemStyle-Width="100px">
                                        <ItemTemplate>
                                            <asp:LinkButton ID="lnkPatientDetails" CssClass="label1" Style="width: 100%" runat="server"
                                                OnClick="lnkPatientDetails_Click"> 
                                    <i class="fa fa-info-circle"  aria-hidden="true" id="varshu_'<%# Eval("Patient_ID") %>'"></i></asp:LinkButton>
                                        </ItemTemplate>
                                    </asp:TemplateField>--%>
                                    <asp:TemplateField HeaderText="Action" ItemStyle-Width="300px" ItemStyle-CssClass="Headerpad">
                                        <ItemTemplate>
                                            <asp:LinkButton ID="lnkSave" CssClass="label1 fntsz" Style="width: 100%; padding: 0px"
                                                runat="server" OnClick="lnkSave_Click" OnClientClick="return validateSaveAppointment(this) "> 

                                                <i class="fa fa-floppy-o lbl-black" data-toggle="tooltip" title="Save" aria-hidden="true"></i></asp:LinkButton>
                                            &nbsp;
                                            <asp:LinkButton ID="lnkDelete" CssClass="label1 fntsz" Style="width: 100%; padding: 0px"
                                                runat="server" OnClick="lnkDelete_Click" OnClientClick="return validateDeleteAppointment(this);"> 
                                                <i class="fa fa-trash-o" data-toggle="tooltip" title="Delete" aria-hidden="true"></i></asp:LinkButton>
                                            &nbsp;
                                            <asp:LinkButton ID="lnkPatientDetails" CssClass="label1 fntsz" Style="width: 100%;
                                                padding: 0px" runat="server" OnClick="lnkPatientDetails_Click"> 
                                              <i class="fa fa-info-circle"  data-toggle="tooltip" title="Visit Detail" aria-hidden="true" id="varshu_'<%# Eval("Patient_ID") %>'"></i></asp:LinkButton>
                                            &nbsp;
                                            <asp:LinkButton ID="lnkReBook" CssClass="label1 fntsz" Style="width: 100%; padding: 0px"
                                                runat="server" OnClick="lnkReBook_Click" Visible="false"> <i class="fa fa-clock-o" data-toggle="tooltip" title="ReBook" aria-hidden="true"></i></asp:LinkButton>
                                            &nbsp;
                                            <asp:LinkButton ID="lnkAction" CssClass="label1 fntsz" Style="width: 100%; padding: 0px"
                                                runat="server" CommandName="view" CommandArgument='<%# Eval("Patient_ID") %>'> <i class="fa fa-forward" data-toggle="tooltip" title="Treatment" aria-hidden="true"></i></asp:LinkButton>
                                        </ItemTemplate>
                                    </asp:TemplateField>
                                </Columns>
                                <EmptyDataTemplate>
                                    <center>
                                        <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                </EmptyDataTemplate>
                                <AlternatingRowStyle BackColor="White" />
                                <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                <PagerStyle BackColor="#3a6f9f" ForeColor="White" HorizontalAlign="Center"></PagerStyle>
                                <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                            </asp:GridView>
                            <triggers>
                <asp:AsyncPostBackTrigger ControlID="GridTimer" EventName="Tick" />
                </triggers>
                        </ContentTemplate>
                    </asp:UpdatePanel>
                </div>
            </div>
        </div>
        <cc3:ModalPopupExtender ID="ReportRecivedPopup" BehaviorID="mpe3" runat="server"
            PopupControlID="Panel2" TargetControlID="btn_Investigation" CancelControlID="btnImg_CloseLabTestResult1"
            BackgroundCssClass="Background">
        </cc3:ModalPopupExtender>
        <asp:Button ID="btn_Investigation" runat="server" Text="" Style="visibility: hidden" />
        <asp:Panel ID="Panel2" runat="server" CssClass="Popup" align="center" Style="display: none;">
            <asp:UpdatePanel ID="UpdatePanel1" runat="server">
                <%-- <Triggers>
                    <asp:AsyncPostBackTrigger ControlID="grdTest" />
                    <asp:AsyncPostBackTrigger ControlID="btnAddtest" />
                </Triggers>--%>
                <ContentTemplate>
                    <div class="modal-dialog enter-result-modal-adj" runat="server" id="modal" role="document">
                        <div class="modal-content adj_EnterResultPU">
                            <div class="modal-header">
                                <asp:UpdatePanel ID="UpdatePanel5" runat="server">
                                    <ContentTemplate>
                                        <asp:ImageButton ID="btnImg_CloseLabTestResult" runat="server" OnClientClick="return HidePopUp();"
                                            ImageUrl="../images/close_btn_blue.png" CssClass="imgbtn" OnClick="btnImg_CloseLabTestResult_Click" />
                                    </ContentTemplate>
                                </asp:UpdatePanel>
                            </div>
                            <div class="table-responsive">
                                <div class="modal-body popupresult">
                                    <div class="form-group">
                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                            <div class="row">
                                                <div class="col-xs-12 col-sm-12 col-md-12">
                                                    <div class="col-md-10 textalign" id="Div13" style="margin-left: -2%;">
                                                        <b>
                                                            <asp:Label ID="lbl_Patient_Name" CssClass="labelname1 PPlabelname" runat="server"
                                                                Text=""></asp:Label>
                                                            <asp:Label ID="Label9" CssClass="labelname1" runat="server" Text="/"></asp:Label>
                                                            <asp:Label ID="lblReportReceiveGenderName" CssClass="labelname1" runat="server" Text=""></asp:Label>
                                                            <asp:Label ID="Label89" CssClass="labelname1" runat="server" Text="/"></asp:Label>
                                                            <asp:Label ID="lblReportReceiveAgeName" CssClass="labelname1" runat="server" Text=""></asp:Label>
                                                            <asp:Label ID="Label93" CssClass="labelname1" runat="server" Text="Y"></asp:Label>
                                                        </b>
                                                    </div>
                                                    <div class="col-md-2 textalign paddingbothempty-Follow">
                                                        <asp:Label ID="Label43" CssClass="labelbold paddingleft" runat="server" Text="Provider:"
                                                            Style="margin-left: -93%;"></asp:Label>
                                                        <asp:Label ID="lbl_lab_doctor_name" CssClass="lbl-black" runat="server"></asp:Label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br />
                                            <%-- <div class="row">
                                                <div class="col-xs-12 col-sm-12 col-md-12">
                                                    <div class="col-xs-12 col-sm-6 col-md-3">
                                                        <asp:Label ID="lbltestsuggesteddate" CssClass="labelbold lbl-patientname-adj cls_left_float"
                                                            runat="server" Text="Lab-Suggested On:">
                                                        </asp:Label>
                                                    </div>
                                                    <div class="col-xs-12 col-sm-6 col-md-3">
                                                        <asp:Label ID="lblSuggesteddate" runat="server" Text="" CssClass="cls_left_float lbl-black paddingleft"></asp:Label>
                                                    </div>
                                                    <div class="col-xs-12 col-sm-6 col-md-3">
                                                        <asp:Label ID="Label7" CssClass="labelbold lbl-patientname-adj cls_right_float" runat="server"
                                                            Text="Lab suggested by:">
                                                        </asp:Label>
                                                        
                                                    </div>
                                                    <div class="col-xs-12 col-sm-6 col-md-3">
                                                        <asp:Label ID="lbl_lab_doctor_name1" style="margin-left: -65px;" CssClass="lbl-black" runat="server"></asp:Label>
                                                    </div>

                                                    <div class="col-xs-12 col-sm-6 col-md-3">
                                                        <asp:Label ID="lblProviderName" runat="server" Text="" CssClass="cls_left_float lbl-black paddingleft"></asp:Label>
                                                    </div>
                                                </div>
                                            </div>--%>
                                            <br />
                                            <div class="row">
                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                    <div class="form-group">
                                                        <div class="col-md-12 textalign">
                                                            <asp:Label ID="lbllabname" Style="margin-left: 1px;" CssClass="labelbold lbl-patientname-adj"
                                                                runat="server" Text="Lab Name"></asp:Label>
                                                            <asp:TextBox ID="txtlabname" runat="server" placeholder="Lab Name" class="form-control cls_left_float lbl-black"
                                                                MaxLength="50"></asp:TextBox>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                    <div class="form-group">
                                                        <div class="col-md-12 textalign">
                                                            <asp:Label ID="lbldoctorname" CssClass="labelbold lbl-patientname-adj" runat="server"
                                                                Text="Lab Doctor Name"></asp:Label>
                                                            <asp:TextBox ID="txtdoctorname" runat="server" placeholder="Doctor Name" class="form-control cls_left_float lbl-black"
                                                                MaxLength="50"></asp:TextBox>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                    <div class="form-group">
                                                        <div class="col-md-12 textalign">
                                                            <asp:Label ID="Label3" CssClass="labelbold lbl-patientname-adj" runat="server" Text="Report Date">
                                                            </asp:Label>
                                                            <asp:TextBox ID="txt_reportdate" runat="server" placeholder="DD-MMM-YYYY" class="form-control cls_left_float lbl-black"></asp:TextBox>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-xs-12 col-sm-12 col-md-12">
                                                    <div class="form-group">
                                                        <div class="col-md-12 textalign">
                                                            <asp:Label ID="Label11" CssClass="labelbold lbl-patientname-adj" runat="server" Text="Comment"></asp:Label>
                                                            <asp:TextBox ID="txt_labcomment" runat="server" placeholder="Comment" class="form-control cls_left_float lbl-black"
                                                                onkeyup="CheckCharacterMedicine(this,1000);" MaxLength="1000"></asp:TextBox>
                                                        </div>
                                                    </div>
                                                    <%-- <div class="col-xs-12 col-sm-6 col-md-3">
                                                        
                                                    </div>
                                                    <div class="col-xs-12 col-sm-6 col-md-3">
                                                        
                                                    </div>--%>
                                                    <%-- <div class="col-xs-12 col-sm-6 col-md-3">
                                                        <div class="input-icon right">--%>
                                                    <%-- </div>
                                                    </div>--%>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-xs-12 col-sm-12 col-md-10">
                                                    <div class="form-group">
                                                        <div class="col-md-12 textalign">
                                                            <asp:Label ID="Label1" CssClass="labelbold" runat="server" Text="">Lab Test</asp:Label>
                                                            <asp:ListBox ID="ddlLabTest" CssClass="setDrop labelddl" runat="server" SelectionMode="Multiple"
                                                                class="form-control cls_left_float lbl-black"></asp:ListBox>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%--<div class="col-xs-12 col-sm-6 col-md-6">--%>
                                                <%--<asp:DropDownList ID="ddlLabTest" runat="server" class="form-control cls_left_float"
                                                            Style="line-height: 28px;">
                                                            <asp:ListItem Selected="True" Value="0" Text="--Select--"></asp:ListItem>
                                                        </asp:DropDownList>--%>
                                                <%--</div>--%>
                                                <div class="col-xs-12 col-sm-6 col-md-2">
                                                    <div class="form-group">
                                                        <div class="col-md-12">
                                                            <br />
                                                            <asp:Button ID="btnAddtest" CssClass="btn btn-primary" runat="server" Width="100px"
                                                                Text="Add Results" OnClientClick="return Valid();" />
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div>
                                                <asp:Label ID="lblTestDuplicateError" runat="server" CssClass="error-box" Text=""></asp:Label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                            <div class="col-sm-12">
                                                <asp:Label ID="lblTestError" runat="server" CssClass="error-box" Text=""></asp:Label>
                                                <asp:Label ID="lblPopupError" runat="server" CssClass="error-box" Text=""></asp:Label>
                                                <asp:Label ID="lblsuccessmessage" runat="server" CssClass="success-message-box" Text=""></asp:Label>
                                                <asp:Label ID="lbldateerror" runat="server" CssClass="error-box" Text=""></asp:Label>
                                            </div>
                                        </div>
                                    </div>
                                    <%-- <br />--%>
                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <div class="test-popup-adjs">
                                                <asp:HiddenField ID="hdnDoctorId" runat="server" />
                                                <asp:HiddenField ID="hdnVisitDate" runat="server" />
                                                <asp:HiddenField ID="hdnVisitNo" runat="server" />
                                                <asp:HiddenField ID="hdnShiftId" runat="server" />
                                                <asp:HiddenField ID="hdnClinicId" runat="server" />
                                                <asp:HiddenField ID="hdnpatientId" runat="server" />
                                                <table id="TableTest" class="table table-bordered">
                                                    <thead>
                                                        <tr>
                                                            <th colspan="4" class="opr_Enter_Result">
                                                                Enter Results
                                                            </th>
                                                        </tr>
                                                        <tr>
                                                            <th class="cls_left_aligned" style="text-align: left; width: 40%;">
                                                                Lab Test Name
                                                            </th>
                                                            <th class="cls_left_aligned" style="text-align: left; width: 30%;">
                                                                Parameter Name
                                                            </th>
                                                            <th class="cls_left_aligned" style="text-align: left; width: 20%;">
                                                                Value / Results
                                                            </th>
                                                            <th style="text-align: center; width: 8%;">
                                                                Action
                                                            </th>
                                                        </tr>
                                                        <asp:HiddenField ID="hdn_cdg_list" runat="server" />
                                                        <div>
                                                        </div>
                                                    </thead>
                                                    <tbody id="body" style="overflow: hidden;" runat="server">
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                    <br />
                                    <br />
                                    <div class="row">
                                        <div class="form-group">
                                            <div class="col-md-4 col-md-offset-3 btn-pop-up">
                                                <center>
                                                    <asp:Button ID="btnsubmit" runat="server" Text="Submit" class="btn btn-primary" OnClientClick="return Generate_ReportReceivedData();"
                                                        OnClick="btnsubmit_Click" />
                                                    <asp:Button ID="btncancel" runat="server" Text="Reset" class="btn btn-primary" OnClick="btncancel_Click" />
                                                    <asp:Button ID="btnCloseLabtest" OnClick="btnImg_CloseLabtest_Click" CssClass="btn btn-primary"
                                                        runat="server" Text="Close" />
                                                </center>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    </div> </div>
                    <asp:Button ID="btnClose3" runat="server" Text="Close" Style="visibility: hidden" />
                </ContentTemplate>
            </asp:UpdatePanel>
        </asp:Panel>
        <asp:UpdatePanel ID="UpdatePanel6" runat="server">
            <Triggers>
                <asp:PostBackTrigger ControlID="grdFilesUploaded" />
            </Triggers>
            <ContentTemplate>
                <cc4:ModalPopupExtender ID="View_Registration_Pop_UP" BehaviorID="mpe4" runat="server"
                    PopupControlID="Panel5" TargetControlID="btnView_Registration" CancelControlID="btnImg_ViewRegistration"
                    BackgroundCssClass="Background">
                </cc4:ModalPopupExtender>
                <asp:Button ID="btnView_Registration" runat="server" Text="" Style="visibility: hidden" />
                <asp:Panel ID="Panel5" runat="server" CssClass="Popup" align="center" Style="display: none">
                    <div class="modal-dialog modal-lg-popup modal-popup-FR" style="margin-left: -50%;">
                        <div class="modal-content">
                            <div class="modal-header">
                                <asp:UpdatePanel ID="UpdatePanel20" runat="server">
                                    <ContentTemplate>
                                        <asp:ImageButton ID="btnImg_ViewRegistration" runat="server" ImageUrl="../images/close_btn_blue.png"
                                            CssClass="imgbtn" />
                                    </ContentTemplate>
                                </asp:UpdatePanel>
                            </div>
                            <br />
                            <div>
                            </div>
                            <ul class="nav nav-tabs nav-justified md-tabs indigo" id="myTabJust" role="tablist"
                                style="font-size: 18px; color: Blue">
                                <li class="nav-item active" id="Personal" style="color: Blue;"><a class="nav-link active"
                                    id="home-tab-just" style="color: Blué;" data-toggle="tab" onclick='ShowPersonalDetail();'
                                    href="#home_just" role="tab" aria-controls="home_just" aria-selected="true">Personal
                                    Details </a></li>
                                <li class="nav-item" id="Medical" style="border-right: 1px solid #66CCCC !important;
                                    border-left: 1px solid #66CCCC !important;"><a class="nav-link" id="profile-tab-just"
                                        data-toggle="tab" href="#profile_just" onclick='ShowPersonalMedical();' role="tab"
                                        aria-controls="profile_just" aria-selected="false">Personal Medical Details
                                    </a></li>
                                <li class="nav-item" id="Family"><a class="nav-link" id="contact-tab-just" data-toggle="tab"
                                    onclick='ShowFamilyMedical();' href="#contact_just" role="tab" aria-controls="contact_just"
                                    aria-selected="false">Family Medical details</a> </li>
                            </ul>
                            <div class="modal-body  maxheight">
                                <div class="tab-content card pt-5" id="myTabContentJust" style="min-height: 400px;
                                    border-color: #ffffff !important; padding: 0px 20px;">
                                    <div class="tab-pane fade active in " id="home_just" role="tabpanel" aria-labelledby="home-tab-just">
                                        <asp:UpdatePanel ID="UpdatePanel52" runat="server" UpdateMode="Conditional">
                                            <ContentTemplate>
                                                <div class="row">
                                                    <div class="col-md-12 textalign">
                                                        <div class="labelFull textalign" style="margin-left: 0px">
                                                        </div>
                                                    </div>
                                                </div>
                                                <div id="collapseExample1" class="collapse in">
                                                    <div class="row">
                                                        <div class="col-md-12">
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Patient Id</label>
                                                                            <asp:Label ID="lblpatientid" disabled="disabled" class="form-control cls_right_aligned lbl-patient-id-adj FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Folder Id</label>
                                                                            <asp:Label ID="lblfolderid" class="form-control cls_right_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Last Name</label>
                                                                            <asp:Label ID="lbllname" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                First Name</label>
                                                                            <asp:Label ID="lblfname" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Middle Name</label>
                                                                            <asp:Label ID="lblmname" class="form-control cls_left_aligned FR_PP_Folder" runat="server"
                                                                                Text=""></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Date of birth</label>
                                                                            <div class="input-icon right">
                                                                                <i class="fa fa-calendar"></i>
                                                                                <asp:Label ID="lblDateOfBirth" class="form-control FR-PP-fa-calender-label FR_PP_Folder"
                                                                                    runat="server"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Age</label>
                                                                            <asp:Label ID="lblAge" class="form-control cls_right_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Gender</label>
                                                                            <asp:Label ID="lblGender" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Address</label>
                                                                            <asp:Label ID="lbladdress1" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Area</label>
                                                                            <div class="input-icon right">
                                                                                <i class="fa fa-search"></i>
                                                                                <asp:Label ID="lblArea" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                City</label>
                                                                            <asp:Label ID="lblcity" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                State</label>
                                                                            <div class="input-icon right">
                                                                                <asp:HiddenField ID="hdnStateId" runat="server" />
                                                                                <asp:Label ID="lblState" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Residential Number</label>
                                                                            <asp:Label ID="lblResiNumber" class="form-control cls_right_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Mobile Number</label>
                                                                            <asp:Label ID="lblmobileno" class="form-control cls_right_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Marital Status</label>
                                                                            <asp:Label ID="lbl_MaritalStatus" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Occupation</label>
                                                                            <asp:Label ID="lblOccupation" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Registration Date</label>
                                                                            <div class="input-icon right">
                                                                                <i class="fa fa-calendar"></i>
                                                                                <asp:Label ID="lblRegistrationDate" class="form-control FR-PP-fa-calender-label FR_PP_Folder"
                                                                                    runat="server"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Blood group</label>
                                                                            <asp:Label ID="lblBloodgroup" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="row" style="text-align: left;">
                                                                        <div class="col-md-10">
                                                                            <label class="label">
                                                                                Height</label>
                                                                            <asp:Label ID="lblHeight1" class="form-control cls_right_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                        <div class="col-md-2 col-md-pull-1" style="margin-top: 20px;">
                                                                            <asp:Label ID="Label20" CssClass="label1111" runat="server" Text="Cm"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="row" style="text-align: left;">
                                                                        <div class="col-md-10">
                                                                            <label class="label">
                                                                                Weight</label>
                                                                            <asp:Label ID="lblWeight1" class="form-control cls_right_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                        <div class="col-md-2 col-md-pull-1" style="margin-top: 20px;">
                                                                            <asp:Label ID="Label88" CssClass="label1111" runat="server" Text="Kg"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Pin Code</label>
                                                                            <asp:Label ID="lblPinCode" class="form-control cls_right_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Email Address</label>
                                                                            <asp:Label ID="lblemail" class="form-control cls_left_aligned FR_PP_Folder" runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label" style="margin-left: -7px;">
                                                                                Emergency ContactName</label>
                                                                            <asp:Label ID="lblEmergencyContactName" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Emergency ContactNo</label>
                                                                            <asp:Label ID="lblEmergencyContactNo" class="form-control cls_right_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label" style="margin-left: 2px;">
                                                                                Refer By</label>
                                                                            <asp:Label ID="lblreferby_ID" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Referral Name</label>
                                                                            <asp:Label ID="lblrefered_doctor_details" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                   <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Referral Contact</label>
                                                                            <asp:Label ID="lbl_DoctorContact" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                     <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Referral Email</label>
                                                                            <asp:Label ID="lblDoctorEmail" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group" style="text-align: left;">
                                                                        <div class="col-md-12">
                                                                            <label class="label">
                                                                                Referral Address</label>
                                                                            <asp:Label ID="lblDoctor_Address" class="form-control cls_left_aligned FR_PP_Folder"
                                                                                runat="server"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                             
                                                           
                                                            </div>
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-2">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12 textalign">
                                                                            <asp:Label ID="Label23" CssClass="label lbl-adjst" runat="server" Style="" Text="Attach Documents :" />
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <%--   <div id="UploadFilepatient" runat="server">--%>
                                                                <div class="col-xs-12 col-sm-12 col-md-7">
                                                                    <div class="form-group">
                                                                        <div class="col-md-6">
                                                                            <div class="table-responsive">
                                                                                <asp:GridView ID="grdFilesUploaded" runat="server" AutoGenerateColumns="false" ShowHeaderWhenEmpty="true"
                                                                                    Width="100%" Height="60px" CellPadding="4" ForeColor="#333333" AllowPaging="true"
                                                                                    PageSize="20">
                                                                                    <AlternatingRowStyle BackColor="White" />
                                                                                    <Columns>
                                                                                        <asp:TemplateField HeaderText="Sr." ItemStyle-Width="20px" ItemStyle-VerticalAlign="Middle"
                                                                                            HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                            <ItemTemplate>
                                                                                                <asp:Label ID="lblSrNo" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                                                            </ItemTemplate>
                                                                                        </asp:TemplateField>
                                                                                        <asp:TemplateField HeaderText="Document Name" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                            HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                            <ItemTemplate>
                                                                                                <asp:HiddenField ID="hdnDocumentID" Value='<%# Bind("Patient_DocumentID") %>' runat="server" />
                                                                                                <asp:HiddenField ID="hdnDocumentPathReg" Value='<%# Bind("PATH") %>' runat="server" />
                                                                                                <asp:LinkButton ID="lnkDocumentname" CssClass="label1" Style="text-decoration: underline"
                                                                                                    OnClientClick="OpenWindowReg(this)" OnClick="lnkDocumentName_Click" CommandName="export"
                                                                                                    runat="server" Text='<%#Bind("Document") %>'></asp:LinkButton>
                                                                                                <asp:Label ID="lblDocumentname" CssClass="label1" runat="server"></asp:Label>
                                                                                            </ItemTemplate>
                                                                                            <ItemStyle Width="300px"></ItemStyle>
                                                                                        </asp:TemplateField>
                                                                                    </Columns>
                                                                                    <EmptyDataTemplate>
                                                                                        <center>
                                                                                            <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                                    </EmptyDataTemplate>
                                                                                    <AlternatingRowStyle BackColor="White" />
                                                                                    <FooterStyle BackColor="#3a6f9f" Font-Bold="True" ForeColor="White" />
                                                                                    <HeaderStyle BackColor="#3a6f9f" ForeColor="White" Font-Names="" />
                                                                                    <PagerSettings Mode="NextPreviousFirstLast" />
                                                                                    <PagerStyle BackColor="#2461BF" ForeColor="White" HorizontalAlign="Center" CssClass="cssPager" />
                                                                                    <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                                    <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                                </asp:GridView>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <%--   </div>--%>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div>
                                                    <asp:Button ID="btnClosePersonal" runat="server" Text="Close" class="btn btn-primary" />
                                                </div>
                                            </ContentTemplate>
                                        </asp:UpdatePanel>
                                    </div>
                                </div>
                                <div class="tab-pane fade" id="profile_just" role="tabpanel" aria-labelledby="profile-tab-just"
                                    style="min-height: 500px; border-color: #ffffff !important; padding: 0px 20px;">
                                    <asp:UpdatePanel ID="UpdatePanel19" runat="server" UpdateMode="Conditional">
                                        <ContentTemplate>
                                            <div class="row">
                                                <div class="col-md-12 textalign">
                                                    <div class="labelFull textalign" style="margin-left: 0px">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="row">
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-3">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="lblChrDisease" CssClass="label paddingleft" runat="server" Text="Chronic Disease: "></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="row palignrowchk">
                                                                    <div class="col-xs-6 col-sm-4 col-md-2 hyp-lbl-adjst hyp-adjst">
                                                                        <div class="">
                                                                            <div class="col-sm-12">
                                                                                <asp:Label ID="lblhyper" CssClass="labelbold" runat="server" Text="Hypertension:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 phyp">
                                                                        <div class="form-group">
                                                                            <div class="col-sm-12">
                                                                                <asp:CheckBox ID="chkPerHypertension" Style="margin-right: -28px" runat="server"
                                                                                    Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2">
                                                                        <div class="">
                                                                            <div class="col-sm-12">
                                                                                <asp:Label ID="lblDiab" CssClass="labelbold" runat="server" Text="Diabetes:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-1 col-md-1 pDiabetes_chk ">
                                                                        <div class="form-group">
                                                                            <div class="col-sm-12">
                                                                                <asp:CheckBox ID="chkPerDiabetes" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2 viewcho-lbl-adjst">
                                                                        <div class="">
                                                                            <div class="col-md-12">
                                                                                <asp:Label ID="lblCholestrrol" CssClass="labelbold" runat="server" Text=" Cholesterol:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pChol">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkPerCholesterol" Style="margin-left: 9px" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <%--               <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-3 hidden-xs hidden-sm">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>--%>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <%--  <div class="col-xs-12 col-sm-12 col-md-3">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="lblhabits" CssClass="label paddingleft" Style="float: left;" runat="server"
                                                                                Text="Social History:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                                <div class="row palignrowchk">
                                                                    <div class="col-xs-6 col-sm-4 col-md-2 smoke-lbl-adjst smoke-adjst">
                                                                        <div class="">
                                                                            <div class="col-md-12" style="margin-left: -47px;">
                                                                                <asp:Label ID="lblSmoking" CssClass="labelbold" runat="server" Text="Smoking:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkPerSmoking" Style="margin-left: -113px;" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2">
                                                                        <div class="">
                                                                            <div class="col-md-12">
                                                                                <asp:Label ID="lbltobacco" CssClass="labelbold" runat="server" Text="Tobacco:" Style="margin-left: -26px;"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pTobacco_chk">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkPerTobacco" runat="server" Enabled="false" Style="margin-left: -58px;" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2 viewalch-lbl-adjst">
                                                                        <div class="">
                                                                            <div class="col-md-12">
                                                                                <asp:Label ID="lblalcohol" CssClass="labelbold" runat="server" Text="Alcohol:" Style="margin-left: -31px;"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pAlcohol_chk">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12" style="margin-left: -39px;">
                                                                                <asp:CheckBox ID="chkPerAlcohol" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-md-6 textalign">
                                                    <div class="row">
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <div class="row">
                                                                    <div class="col-xs-6 col-sm-4 col-md-1 ihd-lbl-adjst ihd-adjst">
                                                                        <div class="">
                                                                            <div class="col-md-12">
                                                                                <asp:Label ID="lblIH" CssClass="labelbold" runat="server" Text="IHD:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pIHD_chk">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12" style="margin-left: -7px;">
                                                                                <asp:CheckBox ID="chkPerIhd" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2">
                                                                        <div class="">
                                                                            <div class="">
                                                                                <asp:Label ID="lblBAsthma" CssClass="labelbold lable-adjs" runat="server" Text="Asthma:"
                                                                                    Style="margin-left: 25px;"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pAsthma_chk">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkPerBrAsthma" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-1 viewth-lbl-adjst">
                                                                        <div class="">
                                                                            <div class="col-md-12">
                                                                                <asp:Label ID="lblPerTh" CssClass="labelbold" runat="server" Text="TH:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pTH_chk">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkPerTh" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <asp:Label ID="lblhabits" CssClass="label paddingleft" Style="float: left; margin-left: 26px;"
                                                                        runat="server" Text="Social History:"></asp:Label>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-3 hidden-xs hidden-sm">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-8">
                                                                    <div class="form-group">
                                                                        <%--  <div class="col-md-12">
                                                                            <asp:Label ID="lblAddictionComment" CssClass="label plbl-adjst" Style="" runat="server"
                                                                                Text="Past Medical History:"></asp:Label>
                                                                        </div>--%>
                                                                        <%--    <asp:Label ID="lblAddictionComment" CssClass="label plbl-adjst" Style="" runat="server"
                                                                                Text="Past Medical History:"></asp:Label>
                                                                                  <asp:TextBox ID="txtHabits" MaxLength="500" runat="server" ReadOnly="true" class="form-control multitextarea"
                                                                                TextMode="MultiLine"></asp:TextBox>--%>
                                                                    </div>
                                                                </div>
                                                                <%--      <div class="col-xs-12 col-sm-12 col-md-8">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtHabits" MaxLength="500" runat="server" ReadOnly="true" class="form-control multitextarea"
                                                                                TextMode="MultiLine"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                            </div>
                                                        </div>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-3 hidden-xs hidden-sm">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <%--   <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="lblPreviousSergery" CssClass="label plbl-adjst" Style="" runat="server"
                                                                                Text="Past Surgical History:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-8">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtPreviousSergery" MaxLength="250" ReadOnly="true" runat="server"
                                                                                class="form-control multitextarea" TextMode="MultiLine"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>--%>
                                                        </div>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <%--           <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="lblMedicines" CssClass="label plbl-adjst" Style="" runat="server"
                                                                                Text="Current Medicines:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-8">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtMedicines" MaxLength="250" runat="server" ReadOnly="true" class="form-control multitextarea"
                                                                                TextMode="MultiLine"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>--%>
                                                        </div>
                                                    </div>
                                                    <!------row end ----->
                                                </div>
                                                <!------col-6 end ----->
                                            </div>
                                            <div class="col-xs-12 col-sm-12 col-md-12 col-md-12">
                                                <%--   <div class="col-xs-12 col-sm-12 col-md-4">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12 textalign">
                                                                            <asp:Label ID="lblCurrentDisease12" CssClass="label paddingleft" Style="" runat="server"
                                                                                Text="Visit Comments:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-7">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtCurrentDisease" MaxLength="250" ReadOnly="true" runat="server"
                                                                                class="form-control multitextarea" TextMode="MultiLine"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="margin-left: -17px; text-align: left;">
                                                    <asp:Label ID="lblCurrentDisease12" CssClass="label" Style="" runat="server" Text="Visit Comments"></asp:Label>
                                                    <asp:TextBox ID="txtCurrentDisease" MaxLength="250" ReadOnly="true" runat="server"
                                                        class="form-control multitextarea" TextMode="MultiLine"></asp:TextBox>
                                                </div>
                                                <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="float: left; text-align: left;">
                                                    <asp:Label ID="lblallergies" CssClass="label" runat="server" Text="Allergies / Vital Comments"></asp:Label>
                                                    <asp:TextBox ID="txtAllergyArea" MaxLength="250" runat="server" ReadOnly="true" TextMode="MultiLine"
                                                        class="form-control multitextarea allergytext"></asp:TextBox>
                                                </div>
                                                <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="text-align: left;">
                                                    <asp:Label ID="lblAddictionComment" CssClass="label" Style="" runat="server" Text="Past Medical History"></asp:Label>
                                                    <asp:TextBox ID="txtHabits" MaxLength="500" runat="server" ReadOnly="true" class="form-control multitextarea"
                                                        TextMode="MultiLine"></asp:TextBox>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-sm-12 col-md-12 " style="margin-top: 21px;">
                                                <div class="row">
                                                    <%--   <div class="col-xs-12 col-sm-12 col-md-4">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="lblallergies" CssClass="label paddingleft" Style="float: left;" runat="server"
                                                                                Text="Allergies / Vital Comments:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-7">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtAllergyArea" MaxLength="250" runat="server" ReadOnly="true" TextMode="MultiLine"
                                                                                class="form-control multitextarea allergytext"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                    <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="margin-left: -7px; text-align: left;">
                                                        <asp:Label ID="lblPreviousSergery" CssClass="label" Style="" runat="server" Text="Past Surgical History"></asp:Label>
                                                        <asp:TextBox ID="txtPreviousSergery" MaxLength="250" ReadOnly="true" runat="server"
                                                            class="form-control multitextarea" TextMode="MultiLine" Style="width: 98%;"></asp:TextBox>
                                                    </div>
                                                    <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="text-align: left;">
                                                        <asp:Label ID="lblMedicines" CssClass="label" Style="" runat="server" Text="Current Medicines"></asp:Label>
                                                        <asp:TextBox ID="txtMedicines" MaxLength="250" runat="server" ReadOnly="true" class="form-control multitextarea"
                                                            TextMode="MultiLine" Style="width: 98%; margin-left: -4px;"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div id="Show_Menstruation_Details" runat="server" visible="false">
                                                <div class="row">
                                                    <div class="col-md-12 textalign">
                                                        <div class="labelFull textalign" style="margin-left: 0px">
                                                            Menstruation History</div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <br />
                                                        <div class="row">
                                                            <div class="col-md-4 textalign">
                                                                <div class="form-group">
                                                                    <div class="col-md-12">
                                                                        <asp:Label ID="Label155" CssClass="labelbold" runat="server" Text="FMP:"></asp:Label>
                                                                        <asp:TextBox ID="txtVRFMP" runat="server" class="form-control multitextarea" TextMode="MultiLine"
                                                                            MaxLength="1000" ReadOnly="true"></asp:TextBox>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4 textalign">
                                                                <div class="form-group">
                                                                    <div class="col-md-12">
                                                                        <asp:Label ID="Label156" CssClass="labelbold" runat="server" Text="PrMC:"></asp:Label>
                                                                        <asp:TextBox ID="txtVRPrMC" runat="server" class="form-control multitextarea" TextMode="MultiLine"
                                                                            MaxLength="1000" ReadOnly="true"></asp:TextBox>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2 textalign">
                                                                <div class="form-group">
                                                                    <div class="col-md-12">
                                                                        <asp:Label ID="Label157" CssClass="labelbold" runat="server" Text="PaMC:"></asp:Label>
                                                                        <asp:TextBox ID="txtVRPaMC" runat="server" class="form-control multitextarea" TextMode="MultiLine"
                                                                            MaxLength="1000" ReadOnly="true"></asp:TextBox>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2 textalign">
                                                                <div class="form-group">
                                                                    <div class="col-md-12">
                                                                        <asp:Label ID="Label158" CssClass="labelbold" runat="server" Text="LMP:"></asp:Label>
                                                                        <asp:TextBox ID="txtVRLMP" runat="server" class="form-control multitextarea" TextMode="MultiLine"
                                                                            MaxLength="1000" ReadOnly="true"></asp:TextBox>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <br />
                                                        <div class="row">
                                                            <div class="col-md-4 textalign">
                                                                <div class="form-group">
                                                                    <div class="col-md-12">
                                                                        <asp:Label ID="Label159" CssClass="labelbold" runat="server" Text="Obstetrics History:"></asp:Label>
                                                                        <asp:TextBox ID="txtVRObs_Hist" runat="server" class="form-control multitextarea"
                                                                            TextMode="MultiLine" MaxLength="1000" ReadOnly="true"></asp:TextBox>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4 textalign">
                                                                <div class="form-group">
                                                                    <div class="col-md-12">
                                                                        <asp:Label ID="Label160" CssClass="labelbold" runat="server" Text="Surgical History / Past History:"></asp:Label>
                                                                        <asp:TextBox ID="txtVR_Surg_Hist" runat="server" class="form-control multitextarea"
                                                                            TextMode="MultiLine" MaxLength="1000" ReadOnly="true"></asp:TextBox>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4 textalign">
                                                                <div class="form-group">
                                                                    <div class="col-md-12">
                                                                        <asp:Label ID="Label161" CssClass="labelbold" runat="server" Text="Additional Comments:"></asp:Label>
                                                                        <asp:TextBox ID="txtVR_Add_Comment" runat="server" class="form-control multitextarea"
                                                                            TextMode="MultiLine" MaxLength="1000" ReadOnly="true"></asp:TextBox>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br />
                                            </div>
                                            <div>
                                                <asp:Button ID="btnCloseMedical" runat="server" Text="Close" class="btn btn-primary"
                                                    Style="margin-top: 24px;" />
                                            </div>
                                        </ContentTemplate>
                                    </asp:UpdatePanel>
                                </div>
                                <div class="tab-pane fade" id="contact_just" role="tabpanel" aria-labelledby="contact-tab-just"
                                    style="max-height: 500px; border-color: #ffffff !important; padding: 0px 20px;">
                                    <asp:UpdatePanel ID="UpdatePanel21" runat="server" UpdateMode="Conditional">
                                        <ContentTemplate>
                                            <div class="row">
                                                <div class="col-md-12 textalign">
                                                    <div class="labelFull textalign" style="margin-left: 0px">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12">
                                                    <div class="row">
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-2" style="margin-left: -16px;">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="lblfamilychronicdisease" CssClass="label paddingleft" Style="" runat="server"
                                                                                Text="Chronic Disease:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="row palignrowchk">
                                                                    <div class="col-xs-6 col-sm-4 col-md-2 hyp-lbl-adjst vhyp-adjst2">
                                                                        <div class="">
                                                                            <div class="col-md-12" style="margin-top: -2px; margin-left: -22px;">
                                                                                <asp:Label ID="lblfhypertension" CssClass="labelbold" runat="server" Text="Hypertension:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pFamily_hyp">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12" style="margin-top: -2px; margin-left: -129px;">
                                                                                <asp:CheckBox ID="chkFamilyHypertension" Style="margin-right: -22px" runat="server"
                                                                                    Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-1">
                                                                        <div class="">
                                                                            <div class="col-md-12" style="margin-left: -134px;">
                                                                                <asp:Label ID="lbl" CssClass="labelbold" runat="server" Text="Diabetes:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12" style="margin-left: -183%;">
                                                                                <asp:CheckBox ID="chkFamilyDiabetes" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-1 viewFamilycho-lbl-adjst">
                                                                        <div class="">
                                                                            <div class="col-md-12" style="margin-left: -191px;">
                                                                                <asp:Label ID="lblFamilyCholesterol" CssClass="labelbold" runat="server" Text="Cholesterol:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pChol">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12" style="margin-left: -221%;">
                                                                                <asp:CheckBox ID="chkFamilyCholesterol" Style="margin-right: -12px" runat="server"
                                                                                    Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2 ">
                                                                        <div class="">
                                                                            <div class="col-md-12" style="margin-left: -282px;">
                                                                                <asp:Label ID="lblFamilyIHD" CssClass="labelbold" runat="server" Text="IHD:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-1 col-md-1 pFamily_IHD_chk" style="margin-left: -410px;">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkFamilyIHD" Style="margin-left: 0px" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2" style="margin-left: -375px">
                                                                        <div class="">
                                                                            <div class="">
                                                                                <asp:Label ID="lblFamilyAsthma" CssClass="labelbold lable-adjs lable-adjs-br" runat="server"
                                                                                    Text="Asthma:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pFamily_Asthma_chk" style="margin-left: -248px">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkFamilyAsthma" Style="margin-left: 16px" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-4 col-md-2 viewFamilyth-lbl-adjst" style="margin-left: -260px">
                                                                        <div class="">
                                                                            <div class="col-md-12">
                                                                                <asp:Label ID="lblFamilyTH" CssClass="labelbold" runat="server" Text="TH:"></asp:Label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-6 col-sm-2 col-md-1 pFamily_TH_chk" style="margin-left: -134px;">
                                                                        <div class="form-group">
                                                                            <div class="col-md-12">
                                                                                <asp:CheckBox ID="chkFamilyTH" Style="margin-left: -90px" runat="server" Enabled="false" />
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                            <div class="row">
                                                                <%--        <div class="col-xs-12 col-sm-12 col-md-3">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12 textalign">
                                                                            <asp:Label ID="lblFamilyHistory" CssClass="label paddingleft" runat="server" Style=""
                                                                                Text="Medical/Family" />
                                                                            <asp:Label ID="lblFamilyHistory123" CssClass="label paddingleft" runat="server" Style=""
                                                                                Text="History:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-8 ptxt-box-adjst2">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtFamilyHistory" MaxLength="150" runat="server" ReadOnly="true"
                                                                                class="form-control multitextarea" TextMode="MultiLine"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                            </div>
                                                        </div>
                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-12 " style="margin-top: 17px;">
                                                <div class="row">
                                                    <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="text-align: left;">
                                                        <asp:Label ID="lblFamilyHistory" CssClass="label paddingleft" runat="server" Style=""
                                                            Text="Medical/Family History" />
                                                        <%--   <asp:Label ID="lblFamilyHistory123" CssClass="label paddingleft" runat="server" Style=""
                                                                                Text=""></asp:Label>--%>
                                                        <asp:TextBox ID="txtFamilyHistory" MaxLength="150" runat="server" ReadOnly="true"
                                                            class="form-control multitextarea" TextMode="MultiLine"></asp:TextBox>
                                                    </div>
                                                    <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="text-align: left;">
                                                        <%--   <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="lblAdditionalCommentFamily" CssClass="label plbl-adjst" Style="" runat="server"
                                                                                Text="Additional Comments:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-8">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtFamilyChronicDisease" MaxLength="500" runat="server" class="form-control multitextarea"
                                                                                ReadOnly="true" TextMode="MultiLine"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>--%>
                                                        <asp:Label ID="lblAdditionalCommentFamily" CssClass="label plbl-adjst" Style="" runat="server"
                                                            Text="Additional Comments"></asp:Label>
                                                        <asp:TextBox ID="txtFamilyChronicDisease" MaxLength="500" runat="server" class="form-control multitextarea"
                                                            ReadOnly="true" TextMode="MultiLine"></asp:TextBox>
                                                    </div>
                                                    <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4" style="text-align: left;">
                                                        <%--    <div class="row">
                                                                <div class="col-xs-12 col-sm-12 col-md-4">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:Label ID="Label162" CssClass="label plbl-adjst" Style="" runat="server" Text="Supportive Tests:"></asp:Label>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-8">
                                                                    <div class="form-group">
                                                                        <div class="col-md-12">
                                                                            <asp:TextBox ID="txtVRSupportivetest" MaxLength="500" runat="server" ReadOnly="true"
                                                                                class="form-control multitextarea" TextMode="MultiLine"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>--%>
                                                        <asp:Label ID="Label162" CssClass="label plbl-adjst" Style="" runat="server" Text="Supportive Tests"></asp:Label>
                                                        <asp:TextBox ID="txtVRSupportivetest" MaxLength="500" runat="server" ReadOnly="true"
                                                            class="form-control multitextarea" TextMode="MultiLine"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div>
                                                <asp:Button ID="btnCloseFamily" runat="server" Text="Close" Style="margin-top: 25px;"
                                                    class="btn btn-primary" />
                                            </div>
                                        </ContentTemplate>
                                    </asp:UpdatePanel>
                                </div>
                            </div>
                            <!------ collapse  end ----->
                        </div>
                    </div>
                </asp:Panel>
            </ContentTemplate>
        </asp:UpdatePanel>
        <asp:UpdatePanel ID="UpdatePanel3" runat="server">
            <ContentTemplate>
                <cc1:ModalPopupExtender ID="FamilyFolderPop_up" BehaviorID="ghh" runat="server" PopupControlID="Panel1"
                    TargetControlID="btn_ptpr" CancelControlID="btnImg_CloseFamilyFolder" BackgroundCssClass="Background">
                </cc1:ModalPopupExtender>
                <asp:Button ID="btn_ptpr" runat="server" Text="" Style="visibility: hidden" />
                <asp:Panel ID="Panel1" runat="server" CssClass="Popup" align="center" Style="display: none;">
                    <div class="modal-dialog compounder-modal-dialog-adjs operator-modal-dialog-adjs">
                        <div class="modal-content" id="modelPrevious">
                            <div class="modal-header">
                                <asp:UpdatePanel ID="UpdatePanel7" runat="server">
                                    <ContentTemplate>
                                        <asp:ImageButton ID="btnImg_CloseFamilyFolder" runat="server" ImageUrl="../images/close_btn_blue.png"
                                            CssClass="imgbtn" />
                                    </ContentTemplate>
                                </asp:UpdatePanel>
                            </div>
                            <div class="table-responsive">
                                <div class="modal-body maxheight">
                                    <div class="table-responsive">
                                        <div class="row">
                                            <div class="col-md-12">
                                                <div class="sublabelFull2" style="text-align: center">
                                                    <b>
                                                        <asp:Label ID="Label2" CssClass="labelbold" Style="font-weight: bold; font-size: 16px;"
                                                            runat="server" Text="Folder Number:"></asp:Label></b>
                                                    <asp:Label ID="lblfolder" CssClass="lbl-black" runat="server"></asp:Label>
                                                </div>
                                                <br />
                                                <asp:GridView ID="grdFamilyFolder" runat="server" AutoGenerateColumns="false" Width="100%"
                                                    Height="70px">
                                                    <Columns>
                                                        <asp:TemplateField HeaderText="Sr." ItemStyle-ForeColor="#075398" ItemStyle-VerticalAlign="Middle"
                                                            HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                            <ItemTemplate>
                                                                <asp:Label ID="lblsrno" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                            </ItemTemplate>
                                                            <ItemStyle Width="5%" />
                                                        </asp:TemplateField>
                                                        <asp:TemplateField HeaderText="Patient Name" ItemStyle-Width="40%" HeaderStyle-CssClass="cls_left_aligned"
                                                            ControlStyle-CssClass="cls_left_float label1">
                                                            <ItemTemplate>
                                                                <asp:Label ID="lblPatientName" runat="server" CssClass="label1" Text='<%# Eval("Name")%>'></asp:Label>
                                                            </ItemTemplate>
                                                        </asp:TemplateField>
                                                        <asp:TemplateField HeaderText="Contact" ItemStyle-Width="10px" HeaderStyle-CssClass="cls_right_aligned"
                                                            ControlStyle-CssClass="cls_left_float label1">
                                                            <ItemTemplate>
                                                                <asp:Label ID="lblMobileNumber" runat="server" CssClass="label1" Text='<%# Eval("Mobile_1")%>'></asp:Label>
                                                            </ItemTemplate>
                                                            <ItemStyle Width="220px"></ItemStyle>
                                                        </asp:TemplateField>
                                                        <asp:TemplateField HeaderText="Age" ItemStyle-Width="50px" HeaderStyle-CssClass="cls_right_aligned"
                                                            ControlStyle-CssClass="cls_right_float label1">
                                                            <ItemTemplate>
                                                                <asp:Label ID="lblDateofBirth" runat="server" CssClass="label1" Text='<%# Eval("AgeYearsIntRound")%>'></asp:Label>
                                                            </ItemTemplate>
                                                            <ItemStyle Width="100px"></ItemStyle>
                                                        </asp:TemplateField>
                                                        <asp:TemplateField HeaderText="Gender" ItemStyle-Width="20px" HeaderStyle-CssClass="cls_left_aligned"
                                                            ControlStyle-CssClass="cls_left_float label1">
                                                            <ItemTemplate>
                                                                <asp:Label ID="lblGender" runat="server" CssClass="label1" Text='<%# Eval("Gender_Description")%>'></asp:Label>
                                                            </ItemTemplate>
                                                            <ItemStyle Width="100px"></ItemStyle>
                                                        </asp:TemplateField>
                                                        <asp:TemplateField HeaderText="Area" ItemStyle-Width="30px" HeaderStyle-CssClass="cls_left_aligned"
                                                            ControlStyle-CssClass="cls_left_float label1">
                                                            <ItemTemplate>
                                                                <asp:Label ID="lblArea" runat="server" CssClass="label1" Text='<%# Eval("Area_Name")%>'></asp:Label>
                                                            </ItemTemplate>
                                                            <ItemStyle Width="450px"></ItemStyle>
                                                        </asp:TemplateField>
                                                    </Columns>
                                                    <EmptyDataTemplate>
                                                        <center>
                                                            <asp:Label ID="lblNoRecordFound" CssClass="label1" runat="server">---</asp:Label></center>
                                                    </EmptyDataTemplate>
                                                    <AlternatingRowStyle BackColor="White" />
                                                    <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                    <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                    <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                    <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                </asp:GridView>
                                                <br />
                                            </div>
                                        </div>
                                        <br />
                                        <div class="row">
                                            <div class="col-md-12">
                                                <asp:Button ID="btnClose" OnClick="btnImg_CloseFamilyFolder_Click" CssClass="btn btn-primary"
                                                    runat="server" Text="Close" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <asp:Button ID="Button1" runat="server" Text="Close" Style="visibility: hidden" />
                </asp:Panel>
            </ContentTemplate>
        </asp:UpdatePanel>
        <asp:UpdatePanel ID="UpdatePanel2" runat="server">
            <ContentTemplate>
                <cc5:ModalPopupExtender ID="ViewPatietDetails" BehaviorID="mpe7" runat="server" PopupControlID="Panel3"
                    TargetControlID="Button2" CancelControlID="ImageButton1" BackgroundCssClass="Background popZindexx">
                </cc5:ModalPopupExtender>
                <asp:Button ID="Button2" runat="server" Text="" Style="visibility: hidden;" />
                <asp:Panel ID="Panel3" runat="server" CssClass="Popup popZindex" align="center" Style="display: none;">
                    <div class="modal-dialog modal-lg-popup">
                        <div class="modal-content">
                            <div class="modal-header">
                                <div class="row">
                                    <div class="col-md-6" style="text-align: left;">
                                        <b>
                                            <asp:Label ID="Label45" runat="server" CssClass="labelname1" Style="font-weight: 600;
                                                margin-left: 2%; color: #075398;" Text="Patient Visit Details"></asp:Label>
                                        </b>
                                    </div>
                                    <div class="col-md-6">
                                        <asp:UpdatePanel ID="UpdatePanel4" runat="server">
                                            <ContentTemplate>
                                                <asp:ImageButton ID="ImageButton14" runat="server" ImageUrl="../images/close_btn_blue.png"
                                                    OnClick="btnImg_viewpatient_deatils" CssClass="imgbtn" />
                                            </ContentTemplate>
                                        </asp:UpdatePanel>
                                    </div>
                                </div>
                            </div>
                            <div class="table-responsive">
                                <div class="modal-body">
                                    <div class="page-content" style="min-height: 30%; margin-top: -3%;">
                                        <%-- <div class="row" style="text-align:left;margin-bottom: 5px;">
                                      
    
                                    </div>--%>
                                        <div class="col-md-12 paddingleft">
                                            <div class="col-md-6 col_impression">
                                                <b>
                                                    <asp:Label ID="lblPatientNameDetails" runat="server" CssClass="labelnamerec paddingleft"></asp:Label>
                                                    <asp:Label ID="Label53" runat="server" CssClass="labelnamerec" Text="/"></asp:Label>
                                                    <asp:Label ID="lblGenderDetails" runat="server" CssClass="labelnamerec"></asp:Label>
                                                    <asp:Label ID="Label56" runat="server" CssClass="labelnamerec" Text="/"></asp:Label>
                                                    <asp:Label ID="lblAgeDetails" runat="server" CssClass="labelnamerec"></asp:Label>
                                                    <asp:Label ID="Label59" runat="server" CssClass="labelnamerec" Text="Y"></asp:Label>
                                                </b>
                                            </div>
                                            <div class="col-md-2 textalign paddingbothempty-Follow" style="margin-top: 6px;margin-left: -1px;">
                                               <%-- <asp:Label ID="Label15" CssClass="labelbold paddingleft" runat="server" Text="Provider:"
                                                    Style="margin-left: -47px;"></asp:Label>--%>
                                                <asp:Label ID="lbl_Patient_Detail_Doctor" CssClass="lbl-black" runat="server"></asp:Label>
                                            </div>
                                            <div class="col-md-2 textalign paddingbothempty-Follow" style="width: 11%; margin-top: 6px;
                                                margin-left: 0px;">
                                                <asp:Label ID="Label40" CssClass="labelbold paddingleft" runat="server" Text="In-Person:"></asp:Label>
                                                <asp:CheckBox runat="server" ID="chk_personin" Checked="true" />
                                            </div>
                                            <div class="col-md-1 textalign paddingbothempty-Follow" style="width: 11.7%; margin-top: 6px;
                                                margin-left: -35px;">
                                                <asp:Label ID="Label18" CssClass="labelbold paddingleft" runat="server" Text="Follow-up:"></asp:Label>
                                                <asp:CheckBox runat="server" ID="chkfollowUp" />
                                            </div>
                                            <div class="col-md-2 textalign paddingbothempty-Follow" style="margin-top: 6px; margin-left: -40px;">
                                                <asp:Label ID="Label17" CssClass="labelbold paddingRight" runat="server" Text="Follow-Up Type:"></asp:Label>
                                                <asp:Label ID="lbl_Patient_Detail_Follow" CssClass="lbl-black" runat="server"></asp:Label>
                                            </div>
                                        </div>
                                        <br />
                                        
                                        <%--  ---------------------------------------------------------------------%>

                                        <div class="row">
                                            <div class="col-xs-12 col-sm-12 col-md-12">
                                            <asp:Label ID="lblErrorReferred" CssClass="errorBox" runat="server"></asp:Label>
                                            </div>
                                            </div>
                                            <br />

                                        <div class="row">
                                            <div class="col-xs-12 col-sm-12 col-md-2">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="Label62" CssClass="labelbold" runat="server" Text="Refer By" Style="float: left;
                                                            margin-left: -4px;"></asp:Label>
                                                        <asp:DropDownList ID="ddlReferBy" runat="server" OnSelectedIndexChanged="ddlReferBy_Changed"
                                                            AutoPostBack="true" class="form-control FR_PP_Folder" Style="line-height: 28px;
                                                            width: 187px; margin-left: -6px;">
                                                        </asp:DropDownList>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-sm-12 col-md-3">
                                             
                                                <div class="col-md-9">
                                                    <div class="form-group">
                                                        <asp:Label ID="Label63" CssClass="labelbold" runat="server" Text="Referral Name" style="margin-left: -60px;"></asp:Label>
                                                        <asp:TextBox ID="txtdoctordetails" runat="server" placeholder="Search with Doctor Name"
                                                            OnSelectedIndexChanged="Referral_Doctor_Selected_Change" class="form-control cls_left_aligned FR_PP_Folder"
                                                            Style="margin-left: 17px; width: 212px;"></asp:TextBox>
                                                        <asp:AutoCompleteExtender ID="txtSearchDoctor_AutocompleteExtender" runat="server"
                                                            Enabled="true" TargetControlID="txtdoctordetails" ServiceMethod="SearchReferralDoctorList"
                                                            ServicePath="~/Services/CMSV2Services.asmx" OnClientItemSelected="DoctorItemSelected"
                                                            MinimumPrefixLength="1">
                                                        </asp:AutoCompleteExtender>
                                                    </div>
                                                </div>
                                              
                                                <div class="col-md-1">
                                                    <asp:LinkButton ID="btnAddDoctor" runat="server" OnClientClick="return validdoctor()"
                                                        OnClick="btnShowAddDoctorPopUp_Click" CssClass="labelname" Style="">
                                                    <i class="fa fa-plus plus-icon cursor glyphi" style="color: white;background-color: #3e77ab;padding: 8px;border-radius: 5px;margin-top: 19px !important;margin-left:20px" data-toggle="tooltip" title="Add New Doctor" aria-hidden="true"></i>                   
                                                    </asp:LinkButton>
                                                </div>
                                                <%--<asp:TextBox ID="txtdoctordetails" runat="server" placeholder="Referral Name" class="form-control cls_left_aligned FR_PP_Folder"></asp:TextBox>--%>
                                            </div>
                                               <asp:UpdatePanel ID="UpdatePanel24" runat="server">
                                               <ContentTemplate>
                                            <div class="col-xs-12 col-sm-12 col-md-2">
                                                <div class="form-group">
                                                    <div class="col-md-12" style="margin-left: 3px;">
                                                        <asp:Label ID="Label65" CssClass="labelbold" runat="server" Text="Referral Contact"
                                                            Style="margin-left: -54px;"></asp:Label>
                                                        <asp:TextBox ID="txtDoctorContact" runat="server" placeholder="Referral Contact"
                                                            MaxLength="10" class="form-control cls_left_aligned FR_PP_Folder" onkeypress="return isNumberKey(event);"
                                                            Style="width: 187px;"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                                        </ContentTemplate>
                                                </asp:UpdatePanel>
                                            <div class="col-xs-12 col-sm-12 col-md-2">
                                                <div class="form-group">
                                                    <div class="col-md-12" style="margin-left: 28px;">
                                                        <asp:Label ID="Label66" CssClass="labelbold" runat="server" Text="Referral Email"
                                                            Style="margin-left: -68px;"></asp:Label>
                                                        <asp:TextBox ID="txtDoctorEmail" runat="server" placeholder="Referral Email" class="form-control cls_left_aligned FR_PP_Folder"
                                                            Style="width: 187px;"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-sm-12 col-md-2">
                                                <div class="form-group">
                                                    <div class="col-md-12" style="margin-left: 52px;">
                                                        <asp:Label ID="Label64" CssClass="labelbold" runat="server" Text="Referral Address"
                                                            Style="margin-left: -54px;"></asp:Label>
                                                        <asp:TextBox ID="txtDoctorAddress" runat="server" placeholder="Referral Address"
                                                            class="form-control cls_left_aligned FR_PP_Folder" Style="width: 187px;"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                           
                                        </div>
                                        <%--  ---------------------------------------------------------------------%>
                                        <div class="col-md-6 col_impression">
                                            <div class="col-md-3 paddingleft">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="lbl_Pulse" CssClass="labelbold" runat="server" Text="Pulse (/min)"></asp:Label>
                                                        <asp:TextBox ID="txtpulse" runat="server" class="form-control" MaxLength="4" onkeypress="return isNumberKeyWeight(event);"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="lbl_Height" CssClass="labelbold" runat="server" Text="Height (Cm)"></asp:Label>
                                                        <asp:TextBox ID="txtHeight" runat="server" class="form-control" MaxLength="3" onkeyup="return BMICalculation()"
                                                            onkeypress="return isNumberKeyWeight(event);"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="lbl_Weight" CssClass="labelbold" runat="server" Text="Weight (Kg)"></asp:Label>
                                                        <asp:TextBox ID="txtweight" runat="server" class="form-control" MaxLength="6" onkeyup="return BMICalculation()"
                                                            onkeypress="return isNumberKeyOnlyWeight(event);"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="Label48" CssClass="labelbold" runat="server" Text="BMI"></asp:Label>
                                                        <asp:TextBox ID="txtBMI" runat="server" class="form-control" MaxLength="6" onkeypress="return isNumberKeyOnlyWeight(event);"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col_impression">
                                            <div class="col-md-4 paddingleft">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="lblBloodPressure" CssClass="labelbold" runat="server" Text="BP"></asp:Label>
                                                        <asp:TextBox ID="txtBP" runat="server" class="form-control" MaxLength="10"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="lbl_Sugar" CssClass="labelbold" runat="server" Text="Sugar"></asp:Label>
                                                        <asp:TextBox ID="txtsugar" runat="server" class="form-control" MaxLength="25"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="lbl_TH" CssClass="labelbold" runat="server" Text="TFT"></asp:Label>
                                                        <asp:TextBox ID="txtTH" runat="server" class="form-control" MaxLength="20"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <br />
                                        <br />
                                        <br />
                                        <br />
                                        <div class="col-md-6 col_impression">
                                            <div class="col-xs-12 col-sm-12 col-md-12 paddingleft">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="Label6" CssClass="labelbold" runat="server" Text="Past Surgical History"></asp:Label>
                                                        <asp:TextBox ID="txtvisitcomments" runat="server" class="form-control" Rows="2" onkeyup="CheckCharacterMedicine(this,1000);"
                                                            TextMode="MultiLine"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col_impression">
                                            <div class="col-xs-12 col-sm-12 col-md-12 paddingleft">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="Label12" CssClass="labelbold" runat="server" Text="Previous Visit Plan"></asp:Label>
                                                        <asp:TextBox ID="txtPreviousVisitTreatment" runat="server" class="form-control" Rows="2"
                                                            TextMode="MultiLine" ReadOnly="true" onkeyup="CheckCharacterMedicine(this,1000);"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col_impression">
                                            <div class="col-xs-12 col-sm-12 col-md-12 paddingleft">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="Label90" CssClass="labelbold" runat="server" Text="Chief complaint entered by patient"></asp:Label>
                                                        <asp:TextBox ID="txtpatientcomplaint" runat="server" Rows="2" class="form-control"
                                                            onkeyup="CheckCharacterMedicine(this,1000);" TextMode="MultiLine" ReadOnly="true"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-12 paddingleft">
                                                <asp:Label ID="lblErrorMsgSymptom" class="error-box" runat="server" Text=""></asp:Label>
                                                <br />
                                            </div>
                                            <div class="col-md-10 paddingleft" style="width: 88%">
                                                <asp:ListBox ID="ddlSymptoms" CssClass="setDrop labelddl" runat="server" SelectionMode="Multiple">
                                                </asp:ListBox>
                                            </div>
                                            <div class="col-md-1 paddingleft">
                                                <asp:Button ID="btnAddSymptoms" CssClass="btn btn-primary" runat="server" Text="Add"
                                                    data-toggle="tooltip" title="Add Complaint" aria-hidden="true" OnClick="btnAddSymptoms_Click" />
                                            </div>
                                        </div>
                                        <div class="col-md-6 col_impression">
                                            <div class="col-xs-12 col-sm-12 col-md-12 paddingleft">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="Label76" CssClass="labelbold" runat="server" Text="Visit Comments"></asp:Label>
                                                        <asp:TextBox ID="txtcurrentcomplaints" runat="server" Rows="2" class="form-control"
                                                            onkeyup="CheckCharacterMedicine(this,1000);" TextMode="MultiLine"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col_impression">
                                            <div class="col-xs-12 col-sm-12 col-md-12 paddingleft">
                                                <div class="form-group">
                                                    <div class="col-md-12">
                                                        <asp:Label ID="Label5" CssClass="labelbold" runat="server" Text="Current Medicines"></asp:Label>
                                                        <asp:TextBox ID="txtcurrentmedicines" runat="server" Rows="2" class="form-control"
                                                            onkeyup="CheckCharacterMedicine(this,1000);" TextMode="MultiLine"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-12 col_impression">
                                            <div class="col-md-6 paddingleft">
                                                <div class="table-responsive" style="padding-bottom: 100px; width: 98%;">
                                                    <asp:UpdatePanel ID="UpdatePanel11" runat="server" UpdateMode="Conditional">
                                                        <ContentTemplate>
                                                            <asp:GridView ID="grdSymptoms" runat="server" AutoGenerateColumns="false" DataKeyNames="Symptoms_Description"
                                                                Height="70px" Style="font-weight: 500;">
                                                                <Columns>
                                                                    <asp:TemplateField HeaderText="Sr." ItemStyle-ForeColor="#075398" ItemStyle-VerticalAlign="Middle"
                                                                        HeaderStyle-CssClass="PPgridDays" ControlStyle-CssClass="cls_right_float gridlabel">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lblsrno" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                                            <asp:HiddenField ID="hdnSymptomId" Value='<%# Bind("ID") %>' runat="server" />
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="3%" />
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="Complaint Description" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float gridlabel wordbreakgrid">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lblSymptomsDescription" CssClass="label1 wordbreakgrid" runat="server"
                                                                                Text='<%#DataBinder.Eval(Container.DataItem, "Complaint_Description") %>'></asp:Label>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="Duration / Comment  " ItemStyle-VerticalAlign="Middle"
                                                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float gridlabel wordbreakgrid"
                                                                        ItemStyle-Width="300px">
                                                                        <ItemTemplate>
                                                                            <asp:TextBox ID="txtComplaintComment" runat="server" class="form-control" Width="100%"
                                                                                Text='<%# Bind("Complaint_Comment") %>' TextMode="MultiLine" Rows="1" onkeyup="CheckCharacterMedicine(this, 250);"></asp:TextBox>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="Action" HeaderStyle-CssClass="cls_left_aligned" ItemStyle-HorizontalAlign="Center"
                                                                        ItemStyle-Width="10px">
                                                                        <ItemTemplate>
                                                                            <%--<asp:LinkButton ID="lnkDeleteSymptom" CommandName="Delete" Style="width: 100%; color: Black;"
                                                                OnClick="lnkDeleteSymptom_onclick" OnClientClick="return confirm('Are you sure you want to delete this Symptom?');"
                                                                runat="server">
                                                        <i class="fa fa-trash-o glyphi" data-toggle="tooltip" title="Delete" aria-hidden="true"></i>
                                                            </asp:LinkButton>--%>
                                                                            <asp:LinkButton ID="lnkDeleteSymptom" CommandName="Delete" Style="width: 100%; color: Black;"
                                                                                OnClick="lnkDeleteSymptom_onclick" runat="server">
                                                                        <i class="fa fa-trash-o glyphi" data-toggle="tooltip" title="Delete" aria-hidden="true"></i>
                                                                            </asp:LinkButton>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="10px"></ItemStyle>
                                                                    </asp:TemplateField>
                                                                </Columns>
                                                                <AlternatingRowStyle BackColor="White" />
                                                                <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                <HeaderStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                <PagerSettings Mode="NextPreviousFirstLast" />
                                                                <PagerStyle BackColor="#2461BF" ForeColor="White" HorizontalAlign="Center" />
                                                                <RowStyle BackColor="#EFF3FB" />
                                                                <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                            </asp:GridView>
                                                        </ContentTemplate>
                                                    </asp:UpdatePanel>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-4 col_impression">
                                                    <span class="col-md-12 paddingleft" style="margin-left: 4%;">
                                                        <div class="col-md-6">
                                                            <asp:Label ID="Label39" class="labelbold" runat="server" Text="Attachments (Max 3):"></asp:Label><br />
                                                        </div>
                                                        <%--  File Upload Changed buy Apoorva--%>
                                                        <div class="col-md-6">
                                                            <asp:UpdatePanel ID="UpdatePanel14" runat="server" UpdateMode="Conditional">
                                                                <ContentTemplate>
                                                                    <asp:FileUpload ID="PatientfileUploader" multiple="true" runat="server" onchange="bindattachment(true)" /><%--onchange="bindattachment(true)"--%>
                                                                </ContentTemplate>
                                                                <Triggers>
                                                                    <asp:PostBackTrigger ControlID="btnSubmitPatientDetails" />
                                                                </Triggers>
                                                            </asp:UpdatePanel>
                                                        </div>
                                                        <%--  File Upload Changed buy Apoorva--%>
                                                </div>
                                                <div id="UploadFilepatient" runat="server">
                                                    <div>
                                                        <div class="form-group">
                                                            <div class="col-md-6" style="margin-top: 1%;">
                                                                <div class="table-responsive" style="width: 72%;">
                                                                    <asp:GridView ID="grdUploadfiletreatment" runat="server" AutoGenerateColumns="false"
                                                                        ShowHeaderWhenEmpty="true" Width="100%" Height="60px" CellPadding="4" ForeColor="#333333"
                                                                        AllowPaging="true" PageSize="20">
                                                                        <AlternatingRowStyle BackColor="White" />
                                                                        <Columns>
                                                                            <asp:TemplateField HeaderText="Sr." ItemStyle-Width="20px" ItemStyle-VerticalAlign="Middle"
                                                                                HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="lblSrNotre" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                                                </ItemTemplate>
                                                                            </asp:TemplateField>
                                                                            <asp:TemplateField HeaderText="Document Name" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                <ItemTemplate>
                                                                                    <asp:HiddenField ID="hdnDocumentIDtre" Value='<%# Bind("Patient_DocumentID") %>'
                                                                                        runat="server" />
                                                                                    <asp:HiddenField ID="hdnDocumentPathtre" Value='<%# Bind("PATH") %>' runat="server" />
                                                                                    <asp:LinkButton ID="lnkDocumentnametre" CssClass="label1" OnClientClick="OpenWindow(this)"
                                                                                        OnClick="lnkDocumentNametre_Click" Style="text-decoration: underline" CommandName="export"
                                                                                        runat="server" Text='<%#Bind("Document") %>'></asp:LinkButton>
                                                                                    <asp:Label ID="lblDocumentname" CssClass="label1" runat="server"></asp:Label>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="300px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                            <asp:TemplateField HeaderText="Action" HeaderStyle-CssClass="cls_left_aligned" ItemStyle-VerticalAlign="Middle"
                                                                                ItemStyle-Width="10px">
                                                                                <ItemTemplate>
                                                                                    <asp:LinkButton ID="lnkDeleteDocument" CommandName="Delete" Style="width: 100%; color: Blue;"
                                                                                        OnClick="lnkDeleteDocument_onclick" runat="server">
                                                                              <i class="fa fa-trash-o glyphi" data-toggle="tooltip" title="Delete" aria-hidden="true"></i>
                                                                                    </asp:LinkButton>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="10px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                        </Columns>
                                                                        <EmptyDataTemplate>
                                                                            <center>
                                                                                <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                        </EmptyDataTemplate>
                                                                        <AlternatingRowStyle BackColor="White" />
                                                                        <FooterStyle BackColor="#3a6f9f" Font-Bold="True" ForeColor="White" />
                                                                        <HeaderStyle BackColor="#3a6f9f" ForeColor="White" Font-Names="" />
                                                                        <PagerSettings Mode="NextPreviousFirstLast" />
                                                                        <PagerStyle BackColor="#2461BF" ForeColor="White" HorizontalAlign="Center" CssClass="cssPager" />
                                                                        <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                        <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                    </asp:GridView>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <br />
                                        <br />
                                        <br />
                                        <br />
                                        <div class="row">
                                            <div class="col-md-12" style="margin-top: -2%;">
                                                <asp:Label ID="lblErrorPVPopUp" CssClass="errorBox" runat="server"></asp:Label>
                                                <asp:Label ID="lblsuccessPVPopUp" runat="server" CssClass="success-message-box" Text=""></asp:Label>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-12">
                                                <asp:Button ID="btnSubmitPatientDetails" CssClass="btn btn-primary" runat="server"
                                                    OnClientClick="return HidePatientVisitPopUptimeout();" Text="Submit" OnClick="btnSubmitPatientDetails_Click" />
                                                <asp:Button ID="btnCancelPatientDetails" CssClass="btn btn-primary" runat="server"
                                                    Text="Reset" OnClick="btnCancelPatientDetails_Click" />
                                                <asp:Button ID="btnCloseViewPatientDetails" CssClass="btn btn-primary" runat="server"
                                                    Text="Close" OnClick="btnCloseViewPatientDetails_Click" />
                                            </div>
                                        </div>
                                        <!------ collapse mian row end ----->
                                    </div>
                                    <!------ collapse  end ----->
                                </div>
                            </div>
                        </div>
                    </div>
                    <asp:Button ID="btnClosedetails" runat="server" Text="Close" Style="visibility: hidden" />
                </asp:Panel>
            </ContentTemplate>
        </asp:UpdatePanel>
        <asp:UpdatePanel ID="UpdatePanel9" runat="server">
            <ContentTemplate>
                <cc6:ModalPopupExtender ID="ReBookPopUp" BehaviorID="Re4P" runat="server" PopupControlID="Panel4"
                    TargetControlID="Button3" CancelControlID="ImageButton2" BackgroundCssClass="Background">
                </cc6:ModalPopupExtender>
                <asp:Button ID="Button3" runat="server" Text="" Style="visibility: hidden" />
                <asp:Panel ID="Panel4" runat="server" CssClass="Popup" align="center" Style="display: none">
                    <div class="modal-dialog modal-lg-popup adj_Rebook_Popup1">
                        <div class="modal-content" style="min-height: 100%;">
                            <div class="modal-header">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="row" style="text-align: left; margin-left: 5%;">
                                            <b>
                                                <asp:Label ID="Label42" CssClass="labelname1" runat="server" Text="Rebook Patient Appointment"
                                                    Style="margin-left: 1px; color: #075398;"></asp:Label>
                                            </b>
                                        </div>
                                    </div>
                                    <div class="col-md-6 ">
                                        <asp:UpdatePanel ID="UpdatePanel10" runat="server" UpdateMode="Conditional">
                                            <ContentTemplate>
                                                <asp:ImageButton ID="ImageButtone2" runat="server" ImageUrl="../images/close_btn_blue.png"
                                                    OnClick="btnImg_viewpatient_deatils" CssClass="imgbtn" />
                                            </ContentTemplate>
                                        </asp:UpdatePanel>
                                    </div>
                                </div>
                                <div class="table-responsive">
                                    <asp:HiddenField ID="hdn_Validatetime_Params" runat="server" />
                                    <div class="modal-body">
                                        <div class="page-content" style="min-height: 50%">
                                            <br />
                                            <div class="col-md-12 paddingleft">
                                                <div class="col-md-6 col_impression">
                                                    <b>
                                                        <asp:Label ID="lbl_Pat_Name_Rebook" runat="server" CssClass="labelname1 paddingleft"
                                                            Style="margin-left: -6%;"></asp:Label>
                                                        <asp:Label ID="Label10" runat="server" CssClass="labelname1" Text="/"></asp:Label>
                                                        <asp:Label ID="lbl_Gender_Rebook" runat="server" CssClass="labelname1"></asp:Label>
                                                        <asp:Label ID="Label14" runat="server" CssClass="labelname1" Text="/"></asp:Label>
                                                        <asp:Label ID="lbl_Age_Rebook" runat="server" CssClass="labelname1"></asp:Label>
                                                        <asp:Label ID="Label16" runat="server" CssClass="labelname1" Text="Y"></asp:Label>
                                                    </b>
                                                </div>
                                                <div class="col-md-3 textalign paddingbothempty-Follow">
                                                    <asp:Label ID="lblFollowtypeRebbok" CssClass="labelbold paddingRight" runat="server"
                                                        Text="Follow-Up Type:"></asp:Label>
                                                    <asp:Label ID="lblget_followup_type_rbk" CssClass="lbl-black" runat="server"></asp:Label>
                                                </div>
                                                <div class="col-md-3 textalign paddingbothempty-Follow">
                                                    <asp:Label ID="lblfollowdaterebbok" CssClass="labelbold paddingRight" runat="server"
                                                        Text="Follow-Up Date:"></asp:Label>
                                                    <asp:Label ID="lbl_getfollowup_date_rbk" CssClass="lbl-black" runat="server"></asp:Label>
                                                </div>
                                            </div>
                                            <br />
                                            <br />
                                            <div class="col-md-12 paddingleft">
                                                <div class="col-md-12 col_impression">
                                                    <asp:Label ID="lbl_Rebook_DoctorName" CssClass="label paddingleft" runat="server"
                                                        Style="font-size: 16px; margin-left: -3%;"></asp:Label>
                                                </div>
                                                <br />
                                                <br />
                                                <div class="row" style="text-align: left; margin-bottom: 5px;">
                                                    <asp:Label ID="lblcalendername" CssClass="label sel_cal_date" runat="server" Text="Please select Appointment date from calender"></asp:Label>
                                                </div>
                                                <asp:UpdatePanel ID="UpdatePanel12" runat="server" UpdateMode="Conditional">
                                                    <ContentTemplate>
                                                        <div class="row">
                                                            <div class="col-md-6 paddingleft">
                                                                <div class="col-md-8 col_impression">
                                                                    <%-- <table>
                                                                    <tr>
                                                                        <td>--%>
                                                                    <asp:Calendar ID="cntCalendar" runat="Server" Width="100%" SelectionMode="Day" EnableViewState="false"
                                                                        OnSelectionChanged="Calendar1_SelectionChanged" OnVisibleMonthChanged="MonthChange"
                                                                        Format="dd-MMM-yyyy" />
                                                                    <%--       </td>
                                                                    </tr>
                                                                </table>--%>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-6 col_impression">
                                                                <div class="col-md-6 col_impression" style="margin-left: -13px; margin-top: -10px;">
                                                                    <asp:Label ID="Label8" CssClass="label PPlabelname" runat="server" Text="Appointment Date"></asp:Label>
                                                                    <span class="astrick-sign adj_astrick_Pat_Treat" aria-hidden="true">*</span>
                                                                    <div class="input-icon right">
                                                                        <i class="fa fa-calendar" id="Icon_Calendar" runat="server" style="display: none;">
                                                                        </i>
                                                                        <asp:TextBox ID="txtDateOFRebook" runat="server" CssClass="form-control date_text_color"
                                                                            MaxLength="1000" autocomplete="off" placeholder="DD-MMM-YYYY" onclick="onlybackbutton(1);"
                                                                            OnTextChanged="txtDateOFRebook_TextChanged" AutoPostBack="true" ClientIDMode="Static"
                                                                            Style="width: 89%;"></asp:TextBox>
                                                                        <%--  <Ajax:CalendarExtender ID="Cal_Date_Of_Rebbok" Format="dd-MMM-yyyy" TargetControlID="txtDateOFRebook"
                                                        OnClientDateSelectionChanged="SelectDate" OnClientShowing="CurrentDateShowing"
                                                        PopupButtonID="Icon_Calendar" Animated="true" runat="server" CssClass="Calendar"
                                                        BehaviorID="bSelectDate" />--%>
                                                                    </div>
                                                                </div>
                                                                <br />
                                                                <br />
                                                            </div>
                                                            <br />
                                                            <br />
                                                            <br />
                                                            <br />
                                                            <div class="col-md-6 paddingleft" style="margin-left: -15px;">
                                                                <div class="col-md-6 col_impression">
                                                                    <asp:Label ID="Label13" CssClass="label PPlabelname" runat="server" Text="From Time"></asp:Label>
                                                                    <span class="astrick-sign adj_astrick_Pat_Treat" aria-hidden="true">*</span>
                                                                    <div class="col-md-12 paddingleft">
                                                                        <%-- <asp:TextBox ID="txtadmissiontime" runat="server" CssClass="form-control" MaxLength="5" onkeypress="return isNumberTime(event);"
                                            Placeholder="HH:MM" ></asp:TextBox>--%>
                                                                        <div class="col-md-4 paddingleft" style="padding-right: 5px;">
                                                                            <asp:DropDownList ID="ddlBookingHours" runat="server" class="form-control" Style="line-height: 28px;
                                                                                padding: 0px;" onchange="ResetErrorMessage()">
                                                                            </asp:DropDownList>
                                                                        </div>
                                                                        <div class="col-md-4 paddingleft" style="padding-right: 5px;">
                                                                            <asp:DropDownList ID="ddlBookingMinutes" runat="server" class="form-control" Style="line-height: 28px;
                                                                                padding: 0px;" onchange="ResetErrorMessage()">
                                                                            </asp:DropDownList>
                                                                        </div>
                                                                        <div class="col-md-4 paddingleft" style="padding-right: 5px;">
                                                                            <asp:DropDownList ID="ddlBookingTime" runat="server" class="form-control" Style="line-height: 28px;
                                                                                padding: 0px;">
                                                                            </asp:DropDownList>
                                                                        </div>
                                                                        <%-- <asp:Label ID="Label19" CssClass="label PPlabelname" runat="server" Text="To Time"></asp:Label>
                                                       <span class="astrick-sign adj_astrick_Pat_Treat" aria-hidden="true">*</span>

                                                      <div class="col-md-2" style="padding-left:0px">
                                                        <asp:DropDownList ID="ddlBookingHoursto" runat="server" class="form-control" Style="line-height: 28px; padding:0px;"
                                                            onchange="ResetErrorMessage()">
                                                        </asp:DropDownList>
                                                    </div>
                                                    <div class="col-md-2" style="padding-left:0px">
                                                        <asp:DropDownList ID="ddlBookingMinutesto" runat="server" class="form-control" Style="line-height: 28px; padding:0px;"
                                                            onchange="ResetErrorMessage()">
                                                        </asp:DropDownList>
                                                    </div>
                                                    <div class="col-md-2" style="padding-left:0px">
                                                        <asp:DropDownList ID="ddlBookingTimeto" runat="server" class="form-control" Style="line-height: 28px; padding:0px;">
                                                        </asp:DropDownList>
                                                    </div>--%>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-6 col_impression">
                                                                    <asp:Label ID="Label19" CssClass="label PPlabelname" runat="server" Text="To Time"></asp:Label>
                                                                    <span class="astrick-sign adj_astrick_Pat_Treat" aria-hidden="true">*</span>
                                                                    <div class="col-md-12 paddingleft">
                                                                        <%-- <asp:TextBox ID="txtadmissiontime" runat="server" CssClass="form-control" MaxLength="5" onkeypress="return isNumberTime(event);"
                                            Placeholder="HH:MM" ></asp:TextBox>--%>
                                                                        <div class="col-md-4 paddingleft" style="padding-right: 5px;">
                                                                            <asp:DropDownList ID="ddlBookingHoursto" runat="server" class="form-control" Style="line-height: 28px;
                                                                                padding: 0px;" onchange="ResetErrorMessage()">
                                                                            </asp:DropDownList>
                                                                        </div>
                                                                        <div class="col-md-4 paddingleft" style="padding-right: 5px;">
                                                                            <asp:DropDownList ID="ddlBookingMinutesto" runat="server" class="form-control" Style="line-height: 28px;
                                                                                padding: 0px;" onchange="ResetErrorMessage()">
                                                                            </asp:DropDownList>
                                                                        </div>
                                                                        <div class="col-md-4 paddingleft" style="padding-right: 5px;">
                                                                            <asp:DropDownList ID="ddlBookingTimeto" runat="server" class="form-control" Style="line-height: 28px;
                                                                                padding: 0px;">
                                                                            </asp:DropDownList>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <br />
                                                    </ContentTemplate>
                                                </asp:UpdatePanel>
                                            </div>
                                        </div>
                                        <br />
                                        <br />
                                        <div class="row">
                                            <div class="col-md-12">
                                                <asp:Label ID="lblErrorRebook" CssClass="errorBox" runat="server"></asp:Label>
                                                <asp:Label ID="lblSuccessRebook" runat="server" CssClass="success-message-box" Text=""></asp:Label>
                                            </div>
                                        </div>
                                        <br />
                                        <div class="row">
                                            <div class="col-md-12">
                                                <asp:Button ID="btnBook" CssClass="btn btn-primary" runat="server" OnClientClick="return validateBookingform()"
                                                    OnClick="btnBook_Click" Text="Book" />
                                                <asp:Button ID="btnBokkConf" CssClass="btn btn-primary" runat="server" OnClick="btnBookConfirm_Click"
                                                    Text="BookConf" Style="display: none;" />
                                                <asp:Button ID="btnClearBook" CssClass="btn btn-primary" runat="server" Text="Reset"
                                                    OnClick="btnClearBook_Click" />
                                                <asp:Button ID="btnCloseBook" CssClass="btn btn-primary" runat="server" Text="Close"
                                                    OnClick="btnCloseBook_Click" />
                                                <asp:Button ID="btncloseconfr" CssClass="btn btn-primary" runat="server" Text="Close"
                                                    OnClick="btncloseconfr_Click" Style="display: none;" />
                                            </div>
                                        </div>
                                        <br />
                                        <div class="row">
                                            <div class="col-md-12">
                                                <div class="table-responsive">
                                                    <asp:UpdatePanel ID="UpdatePanel56" runat="server">
                                                        <ContentTemplate>
                                                            <asp:GridView ID="grdFutureAppointments" runat="server" AutoGenerateColumns="false"
                                                                Width="100%">
                                                                <Columns>
                                                                    <asp:TemplateField HeaderText="Sr." HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lblsrno" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="3%" />
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="Patient Name" ItemStyle-Width="26%" ItemStyle-VerticalAlign="Middle"
                                                                        SortExpression="Name" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lblNAme" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Name") %>'></asp:Label>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="26%"></ItemStyle>
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="Mobile" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                        SortExpression="Name" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lblmobile" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Mobile") %>'></asp:Label>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="From Time" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                        SortExpression="Name" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lblfromtime" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Visit_Time") %>'></asp:Label>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="To Time" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                        SortExpression="Name" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lbltotime" Text='<%#DataBinder.Eval(Container.DataItem, "From_time") %>'
                                                                                CssClass="label1" runat="server"></asp:Label>
                                                                        </ItemTemplate>
                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                    </asp:TemplateField>
                                                                    <asp:TemplateField HeaderText="Duration (HH:MM)" ItemStyle-Width="19%" HeaderStyle-CssClass="cls_left_aligned"
                                                                        ControlStyle-CssClass="cls_left_float label1">
                                                                        <ItemTemplate>
                                                                            <asp:Label ID="lblduration" Text='<%#DataBinder.Eval(Container.DataItem, "Duration") %>'
                                                                                CssClass="label1" runat="server"></asp:Label>
                                                                        </ItemTemplate>
                                                                    </asp:TemplateField>
                                                                    <%--<asp:TemplateField HeaderText="Time" ItemStyle-Width="100px">
                                                                                    <ItemTemplate>
                                                                                      <asp:Label ID="lblTime" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Visit_Time") %>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                </asp:TemplateField>--%>
                                                                </Columns>
                                                                <EmptyDataTemplate>
                                                                    <center>
                                                                        <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                </EmptyDataTemplate>
                                                                <AlternatingRowStyle BackColor="White" />
                                                                <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                            </asp:GridView>
                                                        </ContentTemplate>
                                                    </asp:UpdatePanel>
                                                </div>
                                            </div>
                                        </div>
                                        <!------ collapse mian row end ----->
                                    </div>
                                    <!------ collapse  end ----->
                                </div>
                            </div>
                        </div>
                    </div>
                    <asp:Button ID="Button8" runat="server" Text="Close" Style="visibility: hidden" />
                </asp:Panel>
            </ContentTemplate>
        </asp:UpdatePanel>
        <%--add prev visit pop up madhura 09-apr-2018--%>
        <asp:UpdatePanel ID="UpdatePanel13" runat="server" UpdateMode="Always">
            <ContentTemplate>
                <cc2:ModalPopupExtender ID="PreviousVisitDetailsPop_up" BehaviorID="mpe1" runat="server"
                    PopupControlID="Panel616" TargetControlID="btn_previsit166" CancelControlID="btnImg_PreviousVisit"
                    BackgroundCssClass="Background">
                </cc2:ModalPopupExtender>
                <asp:Button ID="btn_previsit166" runat="server" Text="" Style="visibility: hidden" />
                <asp:Panel ID="Panel616" runat="server" CssClass="Popup" align="center" Style="display: none">
                    <div class="modal-dialog modal-lg pop_up_resp">
                        <div class="modal-content" id="Div1">
                            <div class="modal-header">
                                <div class="row">
                                    <div class="col-md-6" style="text-align: left;">
                                        <b>
                                            <asp:Label ID="Label44" runat="server" Style="margin-left: 9px; color: #075398;"
                                                CssClass="labelname1" Text="Previous Visits"></asp:Label></b>
                                    </div>
                                    <div class="col-md-6">
                                        <asp:UpdatePanel ID="UpdatePanel18" runat="server">
                                            <ContentTemplate>
                                                <asp:ImageButton ID="btnImg_PreviousVisit" runat="server" ImageUrl="../images/close_btn_blue.png"
                                                    OnClientClick="return HidePopUpPrevious();" CssClass="imgbtn" />
                                            </ContentTemplate>
                                        </asp:UpdatePanel>
                                    </div>
                                </div>
                            </div>
                            <div class="table-responsive" style="margin-top: -20px;">
                                <div class="modal-body">
                                    <div class="row">
                                        <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                                            <div class="row">
                                                <div class="row">
                                                    <div class="col-xs-12 col-sm-12 col-md-6 textalign">
                                                        <div class="col-md-12">
                                                            <asp:Label ID="lblErrorPV" CssClass="labelname error-box" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%-- <br />--%>
                                                <!----------------col 2 end--------------->
                                                <div class=" col-xs-12 col-sm-12 col-md-12">
                                                    <div class="row">
                                                        <div class="col-md-12">
                                                            <%-- <div class="col-md-3">--%>
                                                            <div class="col-md-5" style="margin-left: -1.4%;">
                                                                <div class="pull-left">
                                                                    <b>
                                                                        <asp:Label ID="Label281" runat="server" CssClass="label paddingleft" Text=""></asp:Label></b>
                                                                    <b>
                                                                        <asp:Label ID="lbl_PatientName_PP" runat="server" CssClass="label labelname1 paddingbothempty"></asp:Label>
                                                                        <asp:Label ID="Label261" CssClass="labelname1" runat="server" Text="/"></asp:Label>
                                                                        <asp:Label ID="lblshowgenderPP" CssClass="labelname1" runat="server" Text=""></asp:Label>
                                                                        <asp:Label ID="Label301" CssClass="labelname1" runat="server" Text="/"></asp:Label>
                                                                        <asp:Label ID="lblshowAgePP" CssClass="labelname1" runat="server" Text=""></asp:Label>
                                                                        <asp:Label ID="Label391" CssClass="labelname1" runat="server" Text="Y"></asp:Label>
                                                                    </b>&nbsp;&nbsp;&nbsp;
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4" style="float: left;">
                                                                <div class="col-md-1  textright paddingright">
                                                                    <asp:LinkButton ID="lnk_Next_Date" runat="server" CssClass="labelname cursor" OnClick="lnk_Next_Date_Click"
                                                                        Style="margin-left: -9px;"> 
                                                                       
                                                                        <i class="fa fa-chevron-circle-left adj_PV_Popup_btns" data-toggle="tooltip" title="View Previous Visit" aria-hidden="true"></i>                   
                                                                    </asp:LinkButton>
                                                                </div>
                                                                <div class="col-md-2" style="width: 64%;">
                                                                    <asp:TextBox ID="txtDatespre" runat="server" placeholder="DD-MMM-YYYY" class="form-control cls_center_aligned"
                                                                        Style="display: none" ReadOnly="true"></asp:TextBox>
                                                                    <asp:TextBox ID="txtgetDateNew" runat="server" placeholder="DD-MMM-YYYY" class="form-control cls_center_aligned"
                                                                        ReadOnly="true"></asp:TextBox>
                                                                </div>
                                                                <div class="col-md-1 textalign paddingleft" style="width: 3%">
                                                                    <asp:LinkButton ID="lnk_Previous_Date" runat="server" CssClass="labelname cursor"
                                                                        OnClick="  lnk_Previous_Date_Click "> 
                                                                        
                                                                            <i class="fa fa-chevron-circle-right adj_PV_Popup_btns" data-toggle="tooltip" title="View Next Visit" aria-hidden="true"></i>                   
                                                                    </asp:LinkButton>
                                                                    <asp:HiddenField ID="hdn_Next_Datepre" runat="server" />
                                                                </div>
                                                            </div>
                                                            <div class="col-md-3">
                                                                <div class="col-md-1 textalign" style="margin-top: 6px">
                                                                    <asp:Label ID="lblPLR" runat="server" Style="font-size: 15px; margin-left: -26px;"
                                                                        CssClass="lbl-black wordbreakgrid  PLR_style" Text=""></asp:Label>
                                                                </div>
                                                                <div class="col-md-9 col-md-offset-1 textalign" style="margin-top: 6px">
                                                                    <asp:Label ID="lblPrevDoctorName" runat="server" Style="font-size: 15px; margin-left: -22%;"
                                                                        CssClass="lbl-black wordbreakgrid  PLR_style" Text=""></asp:Label>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <%-- <div class="row" style="margin-top: 17px;"></div>--%>
                                                    <%--<div class="row" style="margin-bottom: 1.6%;">--%>
                                                    <%-- </div>--%>
                                                    <div class="row">
                                                        <%--<div class="col-md-6">
                                                            <div class="pull-left PVPPdate">
                                                                <asp:Label ID="lblDatepre" runat="server" CssClass="label paddingleft" Text="Visit Date:"></asp:Label>
                                                                <asp:Label ID="lblGetDatepre" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                            </div>
                                                        </div>--%>
                                                        <div class="col-md-6">
                                                            <div class="pull-left">
                                                                <asp:Label ID="lbl_AppointmentStatuspre" runat="server" CssClass="label paddingleft"
                                                                    Text="Appointment:" Visible="false"></asp:Label>
                                                                <asp:Label ID="lbl_GetAppointmentStatuspre" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="pull-left PVPPdate">
                                                                <asp:Label ID="lblofflinepre" runat="server" CssClass="label paddingleft" Text="Offline Entry:"
                                                                    Visible="false"></asp:Label>
                                                                <asp:Label ID="lblgetofflinepre" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="pull-left" style="margin-left: -1.3%;">
                                                                <asp:Label ID="lblreferbyPP" CssClass="label" runat="server" Text="Referred By:"
                                                                    Style=""></asp:Label>
                                                                <asp:Label ID="lbl_Referred_ByPP" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                &nbsp;&nbsp;&nbsp;
                                                                <asp:Label ID="lblInPerson" runat="server" CssClass="label" Style="margin-left: 16px"
                                                                    Text="In Person:"></asp:Label>
                                                                <asp:CheckBox ID="chk_InPerson" onclick="return false" runat="server" Style="margin-left: -4px" />
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div id="ShowDetails" runat="server" visible="false">
                                                        <div class="row">
                                                            <div class="row" style="margin-left: 1px;">
                                                                <div class="col-md-12 textalign" style="margin-top: 3px;">
                                                                    <asp:Label ID="Label21" CssClass="label paddingleft" runat="server" Text="Hypertension:"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevHyp" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label22" CssClass="label" runat="server" Text="Diabetes:"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevDiab" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label24" CssClass="label" runat="server" Text="Cholesterol:"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevChol" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label25" CssClass="label" runat="server" Text="IHD:"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevIHD" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label26" CssClass="label" runat="server" Text="Asthma:"></asp:Label>
                                                                    <asp:CheckBox ID="ChkPrevBRAsthma" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label27" CssClass="label" runat="server" Text="TH:"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevTh" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label28" CssClass="label" runat="server" Text="Smoking:"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevSmok" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label29" CssClass="label" runat="server" Text="Tobacco:"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevTobacco" onclick="return false" runat="server" />
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    <asp:Label ID="Label30" CssClass="label" runat="server" Text="Alcohol:" Style="margin-left: 1.6%;"></asp:Label>
                                                                    <asp:CheckBox ID="chkPrevAlch" onclick="return false" runat="server" />
                                                                </div>
                                                            </div>
                                                            <div class="row" style="margin-left: 1px; margin-top: 7px;">
                                                                <%-- <div>
                                                                                            <div class="pull-left">
                                                                                                <asp:Label ID="lblPulse" runat="server"  CssClass="label paddingleft"  Text="Pulse (/min):"></asp:Label>
                                                                                                <asp:TextBox ID="txtGetPulse" runat="server" class="form-control" MaxLength="4" onkeypress="return isNumberKeyWeight(event);"></asp:TextBox>
                                                                                               
                                                                                            </div>
                                                                                        </div>--%>
                                                                <div class="col-md-2" style="width: 15%;">
                                                                    <div class="pull-left">
                                                                        <asp:Label ID="lblHeightpre" CssClass="label paddingleft" runat="server" Text="Height (Cm):"></asp:Label>
                                                                        <asp:Label ID="lblGetHeightpre" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-2" style="width: 15%;">
                                                                    <div class="pull-left">
                                                                        <asp:Label ID="lblWeightpre" CssClass="label" runat="server" Text="Weight (Kg):"></asp:Label>
                                                                        <asp:Label ID="lblGetWeightpre" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-2" style="width: 15%;">
                                                                    <div class="pull-left">
                                                                        <asp:Label ID="lblPulsepre" CssClass="label" runat="server" Text="Pulse (/min):"></asp:Label>
                                                                        <asp:Label ID="lblGetPulsepre" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-2" style="width: 12%;">
                                                                    <div class="pull-left">
                                                                        <asp:Label ID="Label31" CssClass="label" runat="server" Text="BP:"></asp:Label>
                                                                        <asp:Label ID="lblGetBloodPressurepre" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-2" style="width: 17%;">
                                                                    <div class="pull-left">
                                                                        <asp:Label ID="lblSugarpre" CssClass="label" runat="server" Text="Sugar:"></asp:Label>
                                                                        <asp:Label ID="lblGetSugarpre" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-2" style="width: 10%;">
                                                                    <div class="pull-left">
                                                                        <asp:Label ID="Label32" CssClass="label" runat="server" Text="TFT:" Style="margin-left: -30%;"></asp:Label>
                                                                        <asp:Label ID="lblGetTHtextpre" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-2" style="width: 15%;">
                                                                    <div class="pull-left">
                                                                        <asp:Label ID="lbl_Pallorpre" CssClass="label" runat="server" Text="Pallor/HB:" Style="margin-left: -18%;"></asp:Label>
                                                                        <asp:Label ID="lblGetPallorpre" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <br />
                                                            <%--    <br />
                                                            <br />--%>
                                                            <br />
                                                            <div class="row" style="float: left; margin-left: 18px; margin-top: -30px;">
                                                                <asp:Label ID="Label50" CssClass="label PPlabelname" runat="server" Text="Allergy:"></asp:Label>
                                                                <asp:Label ID="txtPrevAllergypre" CssClass="label PPlabelname allergytext" Style="font-size: 12px;"
                                                                    runat="server"></asp:Label>
                                                                <%-- <asp:TextBox ID="txtPrevAllergypre" ReadOnly="true" onkeyup="CheckCharacterMedicine(this,1000);"
                                                                                runat="server" CssClass="form-control multitextarea allergytext" TextMode="MultiLine"
                                                                                MaxLength="1000"></asp:TextBox>--%>
                                                            </div>
                                                            <br />
                                                            <br />
                                                            <div class="row" style="float: left; margin-left: 18px; margin-top: -42px;">
                                                                <asp:Label ID="Label84" CssClass="label PPlabelname" runat="server" Text="Medical History:"></asp:Label>
                                                                <asp:Label ID="txtPrevHabitspre" CssClass="label PPlabelname lbl-black" runat="server"></asp:Label>
                                                            </div>
                                                            <br />
                                                            <div class="row" style="float: left; margin-left: 18px; margin-top: -37px;">
                                                                <asp:Label ID="Label87" CssClass="label PPlabelname" runat="server" Text="Surgical History:"></asp:Label>
                                                                <asp:Label ID="txtPrevVisitCommentspre" CssClass="label PPlabelname lbl-black" runat="server"></asp:Label>
                                                            </div>
                                                            <br />
                                                            <div class="row" style="float: left; margin-left: 18px; margin-top: -32px; text-align: left;">
                                                                <asp:Label ID="Label85" CssClass="label PPlabelname" runat="server" Text="Visit Comments:"></asp:Label>
                                                                <asp:Label ID="txtPrevCurrentComplaintspre" CssClass="label PPlabelname lbl-black"
                                                                    runat="server"></asp:Label>
                                                            </div>
                                                            <br />
                                                            <br />
                                                            <div class="row" style="float: left; margin-left: 18px; margin-top: -32px;text-align: left;">
                                                                <asp:Label ID="Label86" CssClass="label PPlabelname" runat="server" Text="Medicines:"></asp:Label>
                                                                <asp:Label ID="txtPrevCurrentMedicinespre" CssClass="label PPlabelname lbl-black"
                                                                    runat="server"></asp:Label>
                                                            </div>
                                                            <br />
                                                            <div class="row">
                                                                <%--         <div class="col-xs-12 col-sm-12 col-md-2 popupscreenall">
                                                                    <div class="pull-left">
                                                                        <div class="col-md-12 textalign">
                                                                            <asp:Label ID="Label83" CssClass="label PPlabelname" runat="server" Text="Allergy"></asp:Label>
                                                                            <asp:TextBox ID="txtPrevAllergypre" ReadOnly="true" onkeyup="CheckCharacterMedicine(this,1000);"
                                                                                runat="server" CssClass="form-control multitextarea allergytext" TextMode="MultiLine"
                                                                                MaxLength="1000"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                                <%--     <div class="col-xs-12 col-sm-12 col-md-2 popupscreen">
                                                                    <div class="pull-left">
                                                                        <div class="col-md-12 textalign">
                                                                            <asp:Label ID="Label84" CssClass="label PPlabelname" runat="server" Text="Medical History"></asp:Label>
                                                                            <asp:TextBox ID="txtPrevHabitspre" ReadOnly="true" onkeyup="CheckCharacterMedicine(this, 1000);"
                                                                                runat="server" CssClass="form-control multitextarea" TextMode="MultiLine" MaxLength="1000"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                                <%--     <div class="col-xs-12 col-sm-12 col-md-2 popupscreen">
                                                                    <div class="pull-left">
                                                                        <div class="col-md-12 textalign">
                                                                            <asp:Label ID="Label87" CssClass="label PPlabelname" runat="server" Text="Surgical History"></asp:Label>
                                                                            <asp:TextBox ID="txtPrevVisitCommentspre" ReadOnly="true" onkeyup="CheckCharacterMedicine(this, 1000);"
                                                                                runat="server" CssClass="form-control multitextarea" TextMode="MultiLine" MaxLength="1000"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                                <%-- <div class="col-xs-12 col-sm-12 col-md-2 popupscreen">
                                                                    <div class="pull-left">
                                                                        <div class="col-md-12 textalign" style="margin-left: -9%;">
                                                                            <asp:Label ID="Label85" CssClass="label PPlabelname" runat="server" Text="Comments"></asp:Label>
                                                                            <asp:TextBox ID="txtPrevCurrentComplaintspre" ReadOnly="true" onkeyup="CheckCharacterMedicine(this, 1000);"
                                                                                runat="server" CssClass="form-control multitextarea" TextMode="MultiLine" MaxLength="1000"
                                                                                Style="width: 127%;"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                                <%--  <div class="col-xs-12 col-sm-12 col-md-2 popupscreen">
                                                                    <div class="pull-left">
                                                                        <div class="col-md-12 textalign">
                                                                            <asp:Label ID="Label86" CssClass="label PPlabelname" runat="server" Text="Medicines"></asp:Label>
                                                                            <asp:TextBox ID="txtPrevCurrentMedicinespre" ReadOnly="true" onkeyup="CheckCharacterMedicine(this, 1000);"
                                                                                runat="server" CssClass="form-control multitextarea" TextMode="MultiLine" MaxLength="1000"></asp:TextBox>
                                                                        </div>
                                                                    </div>
                                                                </div>--%>
                                                            </div>
                                                            <br />
                                                        </div>
                                                        <div class="row" style="margin-left: 8px; float: left; margin-top: -40px;">
                                                            <div class="col-md-12 paddingleft">
                                                                <asp:Label ID="lblAddCommentspre" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                    Text="Detailed History/Additional Comments:" Style="float: left;"></asp:Label>
                                                                <asp:Label ID="lblGetAdditionalCommentspre" CssClass="lbl-black" runat="server" Text=""
                                                                    Style="word-wrap: break-word; margin-top: 2px;"></asp:Label>
                                                            </div>
                                                        </div>
                                                          <br />
                                                        <div class="row" style="text-align: left;">
                                                            <div class="col-md-3 paddingleft" style="margin-left: 18px; float: left; margin-top: -15px;">
                                                                <asp:Label ID="lblImportantFindingspre" CssClass="label paddingleft align_PV_labels"
                                                                    runat="server" Text="Important/Examination Findings:"></asp:Label>
                                                            </div>
                                                            <div class="col-md-9 " style="margin-left: -74px; float: left; margin-top: -15px;">
                                                                <asp:Label ID="lblGetImportantFindingspre" CssClass="lbl-black" runat="server" Text=""
                                                                    Style="word-wrap: break-word;"></asp:Label>
                                                            </div>
                                                        </div>
                                                        <div class="row" runat="server" id='IdComplaint' style="">
                                                            <asp:Label ID="Label51" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                Text="Complaints:" Style="margin-left: 17px; float: left;"></asp:Label>
                                                            <ul id="uLId" runat="server">
                                                            </ul>
                                                            <%--<div class="col-md-3 paddingleft">
                                                                    <asp:Label ID="Label51" CssClass="label paddingleft align_PV_labels"
                                                                        runat="server" Text="Complaint:"></asp:Label>
                                                                </div>
                                                                <div class="col-md-5 ">
                                                                    <asp:Label ID="lblCompDescription" CssClass="lbl-black" runat="server" Text=""
                                                                        Style="word-wrap: break-word;"></asp:Label>
                                                                </div>
                                                                <div class="col-md-4 ">
                                                                    <asp:Label ID="lblCompComments" CssClass="lbl-black" runat="server" Text=""
                                                                        Style="word-wrap: break-word;"></asp:Label>
                                                                </div>--%>
                                                        </div>
                                                        <div class="row" runat="server" id='DivProvisionalDiagnosis' style="margin-left: 83px;
                                                            margin-top: 1px;">
                                                            <asp:Label ID="Label52" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                Text="Provisional Diagnosis:" Style="margin-left: -76px; float: left;"></asp:Label>
                                                            <ul id="uLProvisionalDiag" runat="server">
                                                            </ul>
                                                        </div>
                                                        <div class="row" style="float: left; margin-left: 7px;">
                                                            <asp:Label ID="lblsymptomcommentpre" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                Text="Examination Comments/Detailed History:" Visible="true"></asp:Label>
                                                            <asp:Label ID="lblGetSymptomCommentpre" runat="server" CssClass="lbl-black wordbreakgrid"
                                                                Text=""></asp:Label>
                                                        </div>
                                                        <br />
                                                        <div class="row" style="text-align: left;">
                                                            <asp:Label ID="lblobservationpre" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                Text="Procedure Performed/Notes:" Visible="true" Style="margin-left: 17px; float: left;
                                                                margin-top: 1px;"></asp:Label>
                                                            <asp:Label ID="lblGetObservationpre" runat="server" CssClass="lbl-black wordbreakgrid"
                                                                Style="float: left; margin-top: 8px;" Text=""></asp:Label>
                                                        </div>
                                                        <br />
                                                        <div class="row" runat="server" id='DivPrescriptions' style="margin-top: -18px; margin-left: -6px;">
                                                            <asp:Label ID="Label54" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                Text="Prescriptions:" Style="margin-left: 13px; float: left;"></asp:Label><br />
                                                            <ul id="uLPrescriptions" runat="server">
                                                            </ul>
                                                        </div>
                                                        <div class="row" runat="server" id='DivLabSuggested' style="margin-top: -4px; margin-left: -6px;">
                                                            <asp:Label ID="Label55" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                Text="Lab Suggested:" Style="margin-left: 14px; float: left;"></asp:Label>
                                                            <ul id="uLLabSuggested" runat="server">
                                                            </ul>
                                                        </div>
                                                        <div class="row" runat="server" id='DivMedicines'>
                                                            <asp:Label ID="Label57" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                Text="Medicines:" Style="margin-left: 18px; float: left;"></asp:Label><br />
                                                            <ul id="uLMedicines" runat="server">
                                                            </ul>
                                                        </div>
                                                        <div class="row" runat="server" id='DivDressing' style="margin-top: -3px;">
                                                            <asp:Label ID="Label58" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                Text="Dressing:" Style="margin-left: 18px; margin-top: -5px; float: left;"></asp:Label>
                                                            <ul id="uLDressing" runat="server">
                                                            </ul>
                                                        </div>
                                                        <div class="row" runat="server" id='DivPreviousProcedure' style="margin-bottom: -10px;">
                                                            <asp:Label ID="Label60" CssClass="label paddingleft align_PV_labels" runat="server"
                                                                Text="Procedure:" Style="margin-left: 18px; margin-top: -2px; float: left;"></asp:Label><br />
                                                            <ul id="uLPreviousProcedure" runat="server">
                                                            </ul>
                                                        </div>
                                                        <br />
                                                        <div class="row">
                                                            <div class="col-xs-12 col-sm-12 col-md-12">
                                                                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6  col_impression" runat="server"
                                                                    id="previous_presymtoms_div">
                                                                    <%-- <div class="pull-left">
                                                                                            <asp:Label ID="lblShowSymptom" Visible="false" runat="server" CssClass="label" Text="Symptoms :"></asp:Label>
                                                                                        </div>--%>
                                                                    <div style="margin-right: 88%;">
                                                                        <asp:Label ID="Label133" CssClass="lbl-RED" runat="server" Font-Bold="true" Text=""></asp:Label>
                                                                    </div>
                                                                    <div class="table-responsive">
                                                                        <asp:GridView ID="grdPreviousSymptoms" AutoGenerateColumns="false" runat="server"
                                                                            Width="100%">
                                                                            <Columns>
                                                                                <asp:TemplateField HeaderText="Complaint" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblSymptomspre" CssClass="label1 wordbreakgrid" runat="server" Text='<%# Eval("Complaint_Description")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                                <asp:TemplateField HeaderText="Duration / Comment" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float gridlabel wordbreakgrid"
                                                                                    ItemStyle-Width="300px">
                                                                                    <ItemTemplate>
                                                                                        <asp:TextBox ID="txtPVComplaintCommentpre" runat="server" class="form-control" Width="100%"
                                                                                            Enabled="false" Text='<%# Bind("Complaint_Comment") %>' TextMode="MultiLine"
                                                                                            Rows="1" onkeyup="CheckCharacterMedicine(this, 250);"></asp:TextBox>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                            </Columns>
                                                                            <EmptyDataTemplate>
                                                                                <center>
                                                                                    <asp:Label ID="lblNoRecordFoundpre1" CssClass="label1 cls_left_float" runat="server"></asp:Label></center>
                                                                            </EmptyDataTemplate>
                                                                            <AlternatingRowStyle BackColor="White" />
                                                                            <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                            <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                            <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                            <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                        </asp:GridView>
                                                                    </div>
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" id="ShowDisease" runat="server">
                                                                    <%-- <div class="pull-left">
                                                                                            <asp:Label ID="lblShowDisease" Visible="false" runat="server" CssClass="label" Text="Disease :"></asp:Label>
                                                                                        </div>--%>
                                                                    <div style="margin-right: 69%;">
                                                                        <asp:Label ID="Label141" CssClass="lbl-RED" runat="server" Font-Bold="true" Text=""></asp:Label>
                                                                    </div>
                                                                    <div class="table-responsive" style="margin-left: -3%;">
                                                                        <asp:GridView ID="grdPreviousDisease" runat="server" AutoGenerateColumns="false"
                                                                            Width="99%">
                                                                            <Columns>
                                                                                <asp:TemplateField HeaderText="Provisional Diagnosis" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblDiseasepre" CssClass="label1 wordbreakgrid" runat="server" Text='<%# Eval("Desease_Description")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                            </Columns>
                                                                            <EmptyDataTemplate>
                                                                                <center>
                                                                                    <asp:Label ID="lblNoRecordFoundpre2" CssClass="label1 cls_left_float" runat="server"></asp:Label></center>
                                                                            </EmptyDataTemplate>
                                                                            <AlternatingRowStyle BackColor="White" />
                                                                            <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                            <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                            <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                            <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                        </asp:GridView>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="row">
                                                            <div class="col-xs-12 col-sm-12 col-md-12">
                                                                <div class="">
                                                                    <div class="col_impression  col-md-6 col-lg-6" runat="server" id="grdMedicinepre">
                                                                        <div style="margin-right: 88%;">
                                                                            <asp:Label ID="Label142" CssClass="lbl-RED" runat="server" Font-Bold="true" Text=""></asp:Label>
                                                                        </div>
                                                                        <div class="table-responsive">
                                                                            <asp:GridView ID="grdExternalMedicine" Width="100%" OnRowDataBound="grdExternalMedicine_rowdatabound"
                                                                                runat="server" AutoGenerateColumns="false">
                                                                                <Columns>
                                                                                    <asp:TemplateField HeaderText="Prescriptions" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblPrescriptionMedicinespre" CssClass="label1" runat="server" Text='<%# Eval("Medicine_Name")%>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                </Columns>
                                                                                <Columns>
                                                                                    <asp:TemplateField HeaderText="B" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblPrescriptionMorningpre" CssClass="label1 wordbreakgrid" runat="server"
                                                                                                Text='<%# Eval("Morning")%>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                    <asp:TemplateField HeaderText="L" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblPrescriptionAfternoonpre" CssClass="label1" runat="server" Text='<%# Eval("Afternoon")%>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                    <asp:TemplateField HeaderText="D" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblPrescriptionNightpre" CssClass="label1" runat="server" Text='<%# Eval("Night")%>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                    <asp:TemplateField HeaderText="Days" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblPrescriptionNoOfDayspre" CssClass="label1" runat="server" Text='<%# Eval("No_Of_Days")%>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                    <asp:TemplateField HeaderText="Instruction" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblPrescriptionInstructionpre" CssClass="label1" runat="server" Text='<%# Eval("Instruction")%>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                </Columns>
                                                                                <EmptyDataTemplate>
                                                                                    <center>
                                                                                        <asp:Label ID="lblNoRecordFoundpre3" CssClass="label1 cls_left_float" runat="server"></asp:Label></center>
                                                                                </EmptyDataTemplate>
                                                                                <AlternatingRowStyle BackColor="White" />
                                                                                <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                                <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                                <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                                <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                            </asp:GridView>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="col-md-6 col-lg-6  ">
                                                                    <div id="grdInvestigation" runat="server">
                                                                        <div style="margin-right: 78%;">
                                                                            <asp:Label ID="Label144" CssClass="lbl-RED" runat="server" Font-Bold="true" Text=""></asp:Label>
                                                                        </div>
                                                                        <div class="table-responsive">
                                                                            <asp:GridView ID="grdInvestigationPV" runat="server" AutoGenerateColumns="false"
                                                                                Width="100%">
                                                                                <Columns>
                                                                                    <asp:TemplateField HeaderText="Lab Suggested" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblInvestigationpre" runat="server" CssClass="label1 wordbreakgrid"
                                                                                                Text='<%# Eval("Lab_Test_Description")%>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                </Columns>
                                                                                <EmptyDataTemplate>
                                                                                    <center>
                                                                                        <asp:Label ID="lblNoRecordFoundpre5" CssClass="label1 cls_left_float" runat="server"></asp:Label></center>
                                                                                </EmptyDataTemplate>
                                                                                <AlternatingRowStyle BackColor="White" />
                                                                                <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                                <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                                <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                                <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                            </asp:GridView>
                                                                        </div>
                                                                    </div>
                                                                    <br />
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="row">
                                                            <div class="col-xs-12 col-sm-12 col-md-12">
                                                                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6  col_impression" runat="server"
                                                                    id="previous_medicine_div">
                                                                    <div style="margin-right: 88%;">
                                                                        <asp:Label ID="Label33" CssClass="lbl-RED" runat="server" Font-Bold="true" Text=""></asp:Label>
                                                                    </div>
                                                                    <div class="table-responsive">
                                                                        <asp:GridView ID="grdInternalMedicine" OnRowDataBound="grdInternalMedicine_rowdatabound"
                                                                            runat="server" Width="100%" AutoGenerateColumns="false">
                                                                            <Columns>
                                                                                <asp:TemplateField HeaderText="Medicines" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblMedicinespre" runat="server" CssClass="label1 wordbreakgrid" Text='<%# Eval("Medicine_Description")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                                <asp:TemplateField HeaderText="B" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblMorningpre" runat="server" CssClass="label1" Text='<%# Eval("Morning")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                                <asp:TemplateField HeaderText="L" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblAfternoonpre" runat="server" CssClass="label1" Text='<%# Eval("Afternoon")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                                <asp:TemplateField HeaderText="D" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblNightpre" runat="server" CssClass="label1" Text='<%# Eval("Night")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                                <asp:TemplateField HeaderText="Days" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblNoOfDayspre" runat="server" CssClass="label1" Text='<%# Eval("No_Of_Days")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                                <asp:TemplateField HeaderText="Instruction" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                    HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                    <ItemTemplate>
                                                                                        <asp:Label ID="lblInstructionpre" runat="server" CssClass="label1" Text='<%# Eval("Instruction")%>'></asp:Label>
                                                                                    </ItemTemplate>
                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                </asp:TemplateField>
                                                                            </Columns>
                                                                            <EmptyDataTemplate>
                                                                                <center>
                                                                                    <asp:Label ID="lblNoRecordFoundpre6" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                            </EmptyDataTemplate>
                                                                            <AlternatingRowStyle BackColor="White" />
                                                                            <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                            <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                            <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                            <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                        </asp:GridView>
                                                                    </div>
                                                                    <br />
                                                                </div>
                                                                <div class="col-xs-12 col-sm-12 col-md-6 col-md-6 col-lg-6" runat="server" id="previous_pregrdDreessing">
                                                                    <div class="row">
                                                                        <div class="col-xs-12 col-sm-12 col-md-12 " runat="server" style="margin-top: -18px;"
                                                                            id="previous_dressing_div">
                                                                            <%--<div class="pull-left">
                                                                                            <asp:Label ID="lblShowDressing" Visible="false" runat="server" CssClass="label" Text="Dressing :"></asp:Label>
                                                                                        </div>--%>
                                                                            <br />
                                                                            <div style="margin-right: 88%;">
                                                                                <asp:Label ID="Label34" CssClass="lbl-RED" runat="server" Font-Bold="true" Text=""></asp:Label>
                                                                            </div>
                                                                            <div class="table-responsive">
                                                                                <asp:GridView ID="grdDreessing" runat="server" AutoGenerateColumns="false" Width="100%">
                                                                                    <Columns>
                                                                                        <asp:TemplateField HeaderText="Dressing" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                            HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                            <ItemTemplate>
                                                                                                <asp:Label ID="lblDressingpre" runat="server" CssClass="label1 wordbreakgrid" Text='<%# Eval("LongDressing_Description")%>'></asp:Label>
                                                                                            </ItemTemplate>
                                                                                            <ItemStyle Width="300px"></ItemStyle>
                                                                                        </asp:TemplateField>
                                                                                    </Columns>
                                                                                    <EmptyDataTemplate>
                                                                                        <center>
                                                                                            <asp:Label ID="lblNoRecordFoundpre9" CssClass="label1 cls_left_float" runat="server"></asp:Label></center>
                                                                                    </EmptyDataTemplate>
                                                                                    <AlternatingRowStyle BackColor="White" />
                                                                                    <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                                    <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                                    <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                                    <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                                </asp:GridView>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <br />
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="row" runat="server" id="div_Pre_Procedure">
                                                            <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
                                                                <div style="margin-right: 71%;">
                                                                    <asp:Label ID="Label143" CssClass="lbl-RED" runat="server" Font-Bold="true" Text=""></asp:Label>
                                                                </div>
                                                                <div class="table-responsive">
                                                                    <asp:GridView ID="grdPreviousProcedure" runat="server" Width="100%" AutoGenerateColumns="false">
                                                                        <Columns>
                                                                            <asp:TemplateField HeaderText="Procedure" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="lblProcedurepre" runat="server" CssClass="label1 wordbreakgrid" Text='<%# Eval("Procedure_Description")%>'></asp:Label>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="300px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                            <asp:TemplateField HeaderText="Findings" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="lblfindingspre" runat="server" CssClass="label1 wordbreakgrid" Text='<%# Eval("Findings_Description")%>'></asp:Label>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="300px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                            <asp:TemplateField HeaderText="Comment" ItemStyle-Width="30px" ItemStyle-VerticalAlign="Middle"
                                                                                HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float gridlabel wordbreakgrid">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="lblFindings_Commentspre" CssClass="label1 wordbreakgrid" runat="server"
                                                                                        Text='<%# Bind("Findings_Comment") %>'></asp:Label>
                                                                                </ItemTemplate>
                                                                            </asp:TemplateField>
                                                                        </Columns>
                                                                        <EmptyDataTemplate>
                                                                            <center>
                                                                                <asp:Label ID="lblNoRecordFound1112" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                        </EmptyDataTemplate>
                                                                        <AlternatingRowStyle BackColor="White" />
                                                                        <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                        <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                        <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                        <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                    </asp:GridView>
                                                                </div>
                                                                <br />
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%-- <div class="row">
                                                                                        <div class="col-xs-12 col-sm-12 col-md-6 col_impression">
                                                                                            <div class="col-md-2">
                                                                                                <asp:Label ID="lblimpression" runat="server" CssClass="label paddingleft align_PV_labels" Text="Impression:"></asp:Label>
                                                                                            </div>
                                                                                            <div class="col-md-10">
                                                                                                <asp:Label ID="lblgetimpression" runat="server" CssClass="lbl-black wordbreakgrid"
                                                                                                    Text=""></asp:Label>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>--%>
                                                <%-- <div class="row">
                                                    <div class="col-xs-12 col-sm-12 col-md-6 col_impression_pp">
                                                        <div class="col-md-1">
                                                            <asp:Label ID="lblInstructionpre" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                Text="Plan:" Visible="true"></asp:Label>
                                                        </div>
                                                        <div class="col-md-8">
                                                            <asp:Label ID="lblGetInstructionpre" runat="server" CssClass="lbl-black wordbreakgrid"
                                                                Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>--%>
                                                <div class="row" id="grdprebalance" runat="server">
                                                    <div class="col-md-1  col-xs-2 adjs-textbox" style="width: 14%; margin-left: 3.1%;">
                                                        <div class="col-md-12 paddingbothempty">
                                                            <asp:Label ID="Label151" CssClass="labelbold" runat="server" Text="" Style="margin-left: -35%;
                                                                font-size: 14px; font-weight: bold;"></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-10 textalign marginleftbalance" style="margin-left: 2%; margin-top: -39px;
                                                        margin-bottom: 15px;">
                                                        <div class="table-responsive">
                                                            <asp:UpdatePanel ID="UpdatePanel15" runat="server">
                                                                <ContentTemplate>
                                                                    <asp:GridView ID="grdbalancePre" runat="server" AutoGenerateColumns="false" Width="82%"
                                                                        Height="70px">
                                                                        <Columns>
                                                                            <%--      <asp:TemplateField HeaderText="Sr." HeaderStyle-CssClass="displaynone" ControlStyle-CssClass="cls_right_float label1">
                                                    <ItemTemplate>
                                                        <asp:Label ID="lblsrno" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                      
                                                    </ItemTemplate>
                                                    <ItemStyle Width="3%" />
                                                </asp:TemplateField>--%>
                                                                            <asp:TemplateField HeaderText="Type" ItemStyle-Width="150px" ItemStyle-VerticalAlign="Middle"
                                                                                SortExpression="Cat_Short_Name" HeaderStyle-CssClass="displaynone" ControlStyle-CssClass="cls_left_float label1">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="lblGroupNamepre" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Balance_Group_Name") %>'></asp:Label>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="150px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                            <asp:TemplateField HeaderText="Sub-Group" ItemStyle-Width="150px" ItemStyle-VerticalAlign="Middle"
                                                                                SortExpression="CatSub_Description" HeaderStyle-CssClass="displaynone" ControlStyle-CssClass="cls_left_float label1">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="lblSubGrouppre" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Balance_Subgroup_Name") %>'></asp:Label>
                                                                                    <asp:Label ID="lbl_hdn_subgrouppre" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Balance_Subgroup_Name") %>'
                                                                                        Visible="false"></asp:Label>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="150px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                            <asp:TemplateField HeaderText="Details" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                SortExpression="Brand_Name" HeaderStyle-CssClass="displaynone" ControlStyle-CssClass="cls_left_float label1">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="lblDetailpre" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Balance_Details") %>'></asp:Label>
                                                                                    <asp:Label ID="lbl_hdnDetailpre" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Balance_Details") %>'
                                                                                        Visible="false"></asp:Label>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="300px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                            <%-- <asp:TemplateField HeaderText="Y/N" ItemStyle-Width="100px" HeaderStyle-CssClass="displaynone" >
                                                    <ItemTemplate>
                                                        <asp:CheckBox ID="radiobtn_Billingpre" runat="server" GroupName="radiogroup" AutoPostBack="true" OnCheckedChanged="radiobtn_BalancePre_CheckedChanged"/>
                                                         
                                                    </ItemTemplate>
                                                </asp:TemplateField>--%>
                                                                            <asp:TemplateField HeaderText="commentspre" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                SortExpression="Brand_Name" HeaderStyle-CssClass="displaynone" ControlStyle-CssClass="cls_left_float label1">
                                                                                <ItemTemplate>
                                                                                    <asp:Label ID="txtcommentspre" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Balance_comment") %>'></asp:Label>
                                                                                    <%-- <asp:TextBox ID="txtcommentspre" runat="server" class="form-control" Width="100%"
                                                            AutoPostBack="true" BorderStyle="None"Text='<%#DataBinder.Eval(Container.DataItem, "Balance_comment") %>'></asp:TextBox>--%>
                                                                                </ItemTemplate>
                                                                                <ItemStyle Width="300px"></ItemStyle>
                                                                            </asp:TemplateField>
                                                                        </Columns>
                                                                        <EmptyDataTemplate>
                                                                            <center>
                                                                                <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                        </EmptyDataTemplate>
                                                                        <AlternatingRowStyle BackColor="White" />
                                                                        <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                                        <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                                        <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                        <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                    </asp:GridView>
                                                                </ContentTemplate>
                                                            </asp:UpdatePanel>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br />
                                                <br />
                                                <div class="row" style="margin-top: -10px;">
                                                    <div class="col-xs-12 col-sm-12 col-md-6 col_impression_pp" style="margin-left: 18px;
                                                        margin-top: -35px;">
                                                        <div class="col-md-1">
                                                            <asp:Label ID="lblInstruction" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                Text="Plan:"></asp:Label>
                                                        </div>
                                                        <br />
                                                        <div class="col-md-8">
                                                            <asp:Label ID="lblGetInstruction" runat="server" CssClass="lbl-black wordbreakgrid"
                                                                Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%--  <div class="row">
                                                    <div class="col-xs-12 col-sm-12 col-md-6 col_impression_pp">
                                                        <div class="col-md-4">
                                                            <asp:Label ID="lblfollow_up_pre" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                Text="Follow-up Comments:"></asp:Label>
                                                        </div>
                                                        <div class="col-md-8">
                                                            <asp:Label ID="lblgetfollow_up_pre" runat="server" CssClass="lbl-black wordbreakgrid"
                                                                Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>--%>
                                                <br />
                                                <div class="row" style="margin-top: 15px;">
                                                    <div class="col-xs-12 col-sm-12 col-md-6 col_impression_pp" style="margin-top: -28px;
                                                        margin-left: 18px;">
                                                        <div class="col-md-2">
                                                            <asp:Label ID="Label139" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                Text="Addendum:"></asp:Label>
                                                        </div>
                                                        <div class="col-md-10">
                                                            <asp:Label ID="lblAddendum" runat="server" Style="color: orange;" CssClass="lbl-black wordbreakgrid"
                                                                Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br />
                                                <div class="row" runat="server" id='DivBilledAmount' style="margin-top: -3px;">
                                                    <%--  <div class="col-md-12">--%>
                                                    <asp:Label ID="Label61" CssClass="label paddingleft align_PV_labels" runat="server"
                                                        Text="Billed (Rs):" Style="margin-left: 33px; float: left;"></asp:Label>
                                                    <asp:Label ID="lblPrevAmountBilled" CssClass="lbl-black" runat="server" Text="" Style="margin-left: 0px;
                                                        margin-top: 3px; margin-right: 10px; float: left;"></asp:Label>
                                                    <ul id="uLBilledAmount" runat="server" style="">
                                                    </ul>
                                                    <%-- </div>--%>
                                                </div>
                                                <div class="row" style="margin-left: 8px;">
                                                    <%--  <div class="col-xs-12 col-sm-12 col-md-3 textalign">
                                                        <div class="textalign">
                                                            <div class="col-md-8 textalign paddingleft">
                                                                <asp:Label ID="Label18AmountToCollect" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                    Text="Billed (Rs):" Style="margin-left: 15px;"></asp:Label>
                                                                <asp:Label ID="lblPrevAmountBilled" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                            </div>
                                                            <div class="col-md-2 textalign">
                                                                <asp:LinkButton ID="lnk_Previous_Visit_Billing" OnClick="lnk_Previous_Visit_Billing_Onclick"
                                                                    runat="server" CssClass="labelname">
                                                                                            <i class="fa fa-plus-circle previous-plus-icon cursor glyphi" data-toggle="tooltip" title="View Billing Details" aria-hidden="true"></i>                   
                                                                </asp:LinkButton>
                                                            </div>
                                                        </div>
                                                    </div>--%>
                                                    <div class="col-xs-12 col-sm-12 col-md-3">
                                                        <div class="pull-left">
                                                            <asp:Label ID="Label7" runat="server" CssClass="label align_PV_labels" Text="Discount (Rs):"
                                                                Style="margin-left: -11%;"></asp:Label>
                                                            <asp:Label ID="lblDiscount" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-xs-12 col-sm-12 col-md-3">
                                                        <div class="pull-left">
                                                            <asp:Label ID="Label49" runat="server" CssClass="label align_PV_labels" Text="Dues (Rs):"
                                                                Style="margin-left: -27%;"></asp:Label>
                                                            <asp:Label ID="lblDues" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-xs-12 col-sm-12 col-md-3" style="margin-left: -16px;">
                                                        <div class="pull-left">
                                                            <asp:Label ID="LabelAmountCollected17" runat="server" CssClass="label align_PV_labels"
                                                                Text="Collected (Rs):"></asp:Label>
                                                            <asp:Label ID="lblPrevAmountCollected" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br />
                                                <div class="row" style="margin-left: 4px; margin-top: -14px;">
                                                    <div class="col-md-3">
                                                        <div class="pull-left">
                                                            <asp:Label ID="lblFollowuptype" CssClass="label paddingleft" Style="margin-left: 4px;"
                                                                runat="server" Text="Follow Up Type:"></asp:Label>
                                                            <asp:Label ID="lblpreFollowupType" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-3" id="followup_comment" runat="server">
                                                        <div class="pull-left">
                                                            <%-- <asp:Label ID="lblFollowupcomment" CssClass="label paddingleft" runat="server" Text="Follow Up comment:"></asp:Label>
  
                                                            <asp:Label ID="lblgetfollow_up_pre" CssClass="lbl-black" runat="server" Text=""></asp:Label>--%>
                                                            <asp:Label ID="Label163" CssClass="label paddingleft pull-left" runat="server" Style="margin-left: -6px;"
                                                                Text="Follow-Up After:"></asp:Label>
                                                            <asp:Label ID="lblfollowupafter" CssClass="lbl-black  pull-left" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-2">
                                                        <div class="pull-left">
                                                            <asp:Label ID="lblFollow_up" CssClass="label" Style="margin-left: -14px;" runat="server"
                                                                Text="Follow Up:"></asp:Label>
                                                            <asp:Label ID="lblFollow_up_Print" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-3" style="margin-left: 93px;">
                                                        <div class="pull-left">
                                                            <asp:Label ID="lblFollowUpDate" CssClass="label paddingleft" Style="margin-left: 6px;"
                                                                runat="server" Text="Follow-up Date:"></asp:Label>
                                                            <asp:Label ID="lblFollowUpDatePrint" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br />
                                                <div class="row" style="margin-top: -15px; margin-left: -7px;">
                                                    <div class="col-md-3">
                                                        <div class="pull-left" style="margin-left: 6%;">
                                                            <asp:Label ID="Label242" CssClass="label paddingleft" runat="server" Text="Receipt No:"></asp:Label>
                                                            <asp:Label ID="lblpreReceiptNumber" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <div class="pull-left">
                                                            <asp:Label ID="Label245" CssClass="label paddingleft" runat="server" Style="margin-left: 3px;"
                                                                Text="Receipt Date:"></asp:Label>
                                                            <asp:Label ID="lblpreReceiptdate" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <div class="pull-left">
                                                            <asp:Label ID="Label246" CssClass="label paddingleft" runat="server" Text="Receipt Amount (Rs):"></asp:Label>
                                                            <asp:Label ID="lblpreReceiptAmount" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br />
                                                <div class="row">
                                                    <div class="col-md-4">
                                                        <div class="pull-left" style="margin-left: 16px; margin-top: -13px;">
                                                            <asp:Label ID="Label47" CssClass="label paddingleft" runat="server" Style="margin-left: 2px;"
                                                                Text="Remark:"></asp:Label>
                                                            <asp:Label ID="getlblPatientAdditional" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br />
                                                <div class="row">
                                                    <div class="col-md-3" id="Pschedules" runat="server" style="margin-left: 15px;">
                                                        <div class="pull-left">
                                                            <%-- <asp:Label ID="lblFollowUpdate" CssClass="label paddingleft" runat="server" Text="Follow Up Date:" style="margin-left: -8%;"></asp:Label>
                                                            <asp:Label ID="GetPrefollowdate" CssClass="lbl-black" runat="server" Text=""></asp:Label>--%>
                                                            <asp:Label ID="Label46" CssClass="label paddingleft pull-left" runat="server" Text="Schedule:"></asp:Label>
                                                            <asp:Label ID="getSchedule" CssClass="lbl-black" runat="server" Text=""></asp:Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%--<br />--%>
                                                <%-- <div class="row">
                                                                                        <div class="col-xs-12 col-sm-12 col-md-6">
                                                                                            <div class="col-md-3 pull-left col_impression">
                                                                                                <asp:Label ID="Label19" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                                                    Text="Payment Type:"></asp:Label>
                                                                                            </div>
                                                                                            <div class="col-md-9 pull-left col_impression">
                                                                                                <asp:Label ID="lblPrevPaymentType" runat="server" CssClass="lbl-black" Text=""></asp:Label>
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="col-xs-12 col-sm-12 col-md-6">
                                                                                            <div class="col-md-3 pull-left col_impression">
                                                                                                <asp:Label ID="Label21" runat="server" CssClass="label align_PV_labels" Text="Payment Remark:"></asp:Label>
                                                                                            </div>
                                                                                            <div class="col-md-9 pull-left col_impression">
                                                                                                <asp:Label ID="lblPrevPaymentRemark" runat="server" CssClass="lbl-black wordbreakgrid"
                                                                                                    Text=""></asp:Label>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>--%>
                                                <div class="row">
                                                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                                                        <div class="col-md-6" runat="server" id="Griduploadfilehide">
                                                            <div class="row">
                                                              <br />
                                                                <div class="col-xs-12 col-sm-12 col-md-6 col_impression" style="margin-left: 1.2%;">
                                                                    <div class="col-md-2" style="margin-left: -2%;">
                                                                        <asp:Label ID="Label149" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                            Text="Attachment (Provider):"></asp:Label>
                                                                    </div>
                                                                </div>
                                                                <!-- preview for uploaded files 18 apr v -->
                                                                <div class="col-xs-12 col-sm-12 col-md-12">
                                                                    <div class="form-group">
                                                                        <!--<span class="asterisk_input3"></span>-->
                                                                        <div class="col-md-12" style="margin-left: -15px;">
                                                                            <asp:GridView ID="GrdPreupload" runat="server" AutoGenerateColumns="false" ShowHeaderWhenEmpty="true"
                                                                                Width="69%" Height="60px" CellPadding="4" ForeColor="#333333" AllowPaging="true"
                                                                                PageSize="20">
                                                                                <AlternatingRowStyle BackColor="White" />
                                                                                <Columns>
                                                                                    <asp:TemplateField HeaderText="Sr." ItemStyle-Width="20px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                        <ItemTemplate>
                                                                                            <asp:Label ID="lblSrNo" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                                                        </ItemTemplate>
                                                                                    </asp:TemplateField>
                                                                                    <asp:TemplateField HeaderText="Document Name" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                        HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                                        <ItemTemplate>
                                                                                            <asp:HiddenField ID="hdnDocumentID" Value='<%# Bind("Patient_DocumentID") %>' runat="server" />
                                                                                            <asp:HiddenField ID="hdnDocumentPathAudio" Value='<%# Bind("PATH") %>' runat="server" />
                                                                                            <asp:LinkButton ID="lnkDocumentnameAudio" CssClass="label1" OnClientClick="OpenWindowAudio(this)"
                                                                                                OnClick="lnkDocumentNamePre_Click" Style="text-decoration: underline" CommandName="export"
                                                                                                runat="server" Text='<%#Bind("Document") %>'></asp:LinkButton>
                                                                                            <asp:Label ID="lblDocumentname" CssClass="label1" runat="server"></asp:Label>
                                                                                        </ItemTemplate>
                                                                                        <ItemStyle Width="300px"></ItemStyle>
                                                                                    </asp:TemplateField>
                                                                                    <%-- <asp:TemplateField HeaderText="Action" ItemStyle-HorizontalAlign="Center" ItemStyle-Width="10px">
                                                                          <ItemTemplate>
                                                                              <asp:LinkButton ID="lnkDeleteDocument" CommandName="Delete" Style="width: 100%; color: Blue;"
                                                                                  OnClick="lnkDeleteDocument_onclick" OnClientClick="return confirm('Are you sure you want to delete this document?');"
                                                                                  runat="server">
                                                                              <i class="fa fa-trash-o glyphi" data-toggle="tooltip" title="Delete" aria-hidden="true"></i>
                                                                              </asp:LinkButton>
                                                                          </ItemTemplate>
                                                                          <ItemStyle Width="10px"></ItemStyle>
                                                                      </asp:TemplateField>--%>
                                                                                </Columns>
                                                                                <EmptyDataTemplate>
                                                                                    <center>
                                                                                        <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                                </EmptyDataTemplate>
                                                                                <AlternatingRowStyle BackColor="White" />
                                                                                <FooterStyle BackColor="#3a6f9f" Font-Bold="True" ForeColor="White" />
                                                                                <HeaderStyle BackColor="#3a6f9f" ForeColor="White" Font-Names="" />
                                                                                <PagerSettings Mode="NextPreviousFirstLast" />
                                                                                <PagerStyle BackColor="#2461BF" ForeColor="White" HorizontalAlign="Center" CssClass="cssPager" />
                                                                                <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                                <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                            </asp:GridView>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div id="grdPreviousefile" runat="server">
                                                                <div class="row">
                                                                    <div class="col-xs-12 col-sm-12 col-md-12 col_impression" style="margin-left: 3.5%;">
                                                                        <div>
                                                                            <div class="form-group">
                                                                                <div style="margin-left: 1%;">
                                                                                    <asp:Label ID="Label41" runat="server" CssClass="label paddingleft align_PV_labels"
                                                                                        Text="Attachment (Operator):"></asp:Label>
                                                                                    <div class="table-responsive" style="margin-left: 12%; width: 72%;">
                                                                                        <asp:GridView ID="grdPrefileupload" runat="server" AutoGenerateColumns="false" ShowHeaderWhenEmpty="true"
                                                                                            Width="100%" Height="60px" CellPadding="4" ForeColor="#333333" AllowPaging="true"
                                                                                            PageSize="20">
                                                                                            <AlternatingRowStyle BackColor="White" />
                                                                                            <Columns>
                                                                                                <asp:TemplateField HeaderText="Sr." ItemStyle-Width="20px" ItemStyle-VerticalAlign="Middle"
                                                                                                    HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                                                    <ItemTemplate>
                                                                                                        <asp:Label ID="lblSrNoprefile" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                                                                    </ItemTemplate>
                                                                                                </asp:TemplateField>
                                                                                                <asp:TemplateField HeaderText="Document Name" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                                                    HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                                                                    <ItemTemplate>
                                                                                                        <asp:HiddenField ID="hdnDocumentIDrefile" Value='<%# Bind("Patient_DocumentID") %>'
                                                                                                            runat="server" />
                                                                                                        <asp:HiddenField ID="hdnDocumentPathrefile" Value='<%# Bind("PATH") %>' runat="server" />
                                                                                                        <asp:LinkButton ID="lnkDocumentnamerefile" CssClass="label1" OnClientClick="OpenWindowfileupload(this)"
                                                                                                            OnClick="lnkDocumentNamefile_Click" Style="text-decoration: underline" CommandName="export"
                                                                                                            runat="server" Text='<%#Bind("Document") %>'></asp:LinkButton>
                                                                                                        <asp:Label ID="lblDocumentname" CssClass="label1" runat="server"></asp:Label>
                                                                                                    </ItemTemplate>
                                                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                                                </asp:TemplateField>
                                                                                            </Columns>
                                                                                            <EmptyDataTemplate>
                                                                                                <center>
                                                                                                    <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                                                                            </EmptyDataTemplate>
                                                                                            <AlternatingRowStyle BackColor="White" />
                                                                                            <FooterStyle BackColor="#3a6f9f" Font-Bold="True" ForeColor="White" />
                                                                                            <HeaderStyle BackColor="#3a6f9f" ForeColor="White" Font-Names="" />
                                                                                            <PagerSettings Mode="NextPreviousFirstLast" />
                                                                                            <PagerStyle BackColor="#2461BF" ForeColor="White" HorizontalAlign="Center" CssClass="cssPager" />
                                                                                            <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                                                            <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                                                        </asp:GridView>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%--<div class="row">
                                                                            
                                                                        </div>--%>
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <center>
                                                            <%-- <asp:Button ID="btnCopyMedicine" CssClass="btn btn-primary" runat="server" Text="Repeat Treatment"
                                                                                        Visible="false" />--%><%-- OnClick="btnCopyMedicine_Click"--%>
                                                            <%-- <asp:Button ID="btnViewInstructions" runat="server" CssClass="btn btn-primary" 
                                                                                Text="View Instructions"></asp:Button> --%><%--OnClick="btnViewInstruction_Click"--%>
                                                            <%--   <asp:Button ID="btnshowlabresults" CssClass="btn btn-primary" runat="server" Text="Lab Results"
                                                                                         Visible="false" />--%><%-- OnClick="btnViewPreviousLabResults_Click"--%>
                                                            <asp:Button ID="btnCloseprevPopup" CssClass="btn btn-primary" runat="server" Text="Close"
                                                                OnClick="btnImg_ClosePreviousVisit_Click" /><%--OnClick="btnImg_ClosePreviousVisit_Click"--%>
                                                        </center>
                                                    </div>
                                                </div>
                                                <br />
                                            </div>
                                            <!----------------col 10 end--------------->
                                        </div>
                                        <!----------------row end ---------------->
                                    </div>
                                    <!----------------col 12 end--------------->
                                </div>
                            </div>
                        </div>
                    </div>
                    </div>
                    <asp:Button ID="btnClose2" runat="server" Text="Close" Style="visibility: hidden" />
                </asp:Panel>
            </ContentTemplate>
        </asp:UpdatePanel>
        <%--  --------------------------------------------------------------------------------%>
        <cc2:ModalPopupExtender ID="AddDoctorToList" BehaviorID="brww" runat="server" PopupControlID="Panel7"
            TargetControlID="btn_ptpr1" CancelControlID="btnClose" BackgroundCssClass="modalBackground">
        </cc2:ModalPopupExtender>
        <asp:Button ID="btn_ptpr1" runat="server" Text="" Style="visibility: hidden" />
        <asp:Panel ID="Panel7" runat="server" CssClass="Popup popZindexAddDoctorToListpopZindexAddDoctorToList"
            align="center" Style="display: none; align: center;">
            <div class="modal-dialog compounder-modal-dialog-adjs operator-modal-dialog-adjs">
                <div class="modal-content" id="Div4">
                    <div class="modal-header">
                        <asp:Label ID="Label67" class="labelbold01" runat="server" Text="Add New Referral Doctor"></asp:Label>
                        <br />
                         <br />
                            <asp:UpdatePanel ID="UpdatePanel22" runat="server">
                           <ContentTemplate>
                        <asp:Label ID="AdddoctorErrorMsg" runat="server" Text="" class="error-box"></asp:Label>
                        <asp:Label ID="AddDoctorSuccess" runat="server" CssClass="success-message-box" Text=""></asp:Label>
                       
                      
                                <asp:ImageButton ID="btnImgclose" runat="server" OnClick="ImageButton1_Click" ImageUrl="../images/close_btn_blue.png"
                                    Style="margin-top: -30px" CssClass="imgbtn" />
                           </ContentTemplate>
                           </asp:UpdatePanel>
                    </div>
                             <asp:UpdatePanel ID="UpdatePanel25" runat="server">
                           <ContentTemplate>
                    <div class="table-responsive" style="height: 372px;">
                        <div class="modal-body maxheight">
                       
                            <div class="row">
                      
                                <div class="col-md-12">
                                    <div id="Div5" runat="server" class="row" style="padding: 5px 0px;">
                                        <div>
                                            <span class="col-md-4 control-label error-box" style="padding: 0px;">
                                            <span class="astrick-sign" aria-hidden="true">*</span>
                                                <asp:Label ID="lblModalReferralName" class="labelbold" runat="server"
                                                    Style="margin-right: 100px;" Text="Doctor Name:"></asp:Label>
                                            </span>
                                            <div class="col-md-6">
                                                <%--<div class="error-box">--%>
                                                <asp:TextBox ID="txtdr" runat="server" class="form-control QRtextWidth" Text="Dr."
                                                    Style="width: 60px; margin-left: -489px" disabled="disabled"></asp:TextBox>
                                                <asp:TextBox ID="txtModalReferralName" runat="server" Style="margin-left: -23px; height:43px;
                                                    width: 421px; margin-top: -43px;" placeholder="Enter Doctor’s name as “ABCD XYZP”"
                                                    class="form-control QRtextWidth" onkeypress="return onlyAlphabets(event,this);"></asp:TextBox>
                                                <span id="reqTxtName" class="reqError"></span>
                                                <%--<asp:RequiredFieldValidator runat="server" Enabled="false" ID="ReqField" controltovalidate="txtModalReferralName" errormessage="Please Enter Doctor Name !!!" />--%>
                                                <%-- </div>--%>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="Div6" runat="server" visible="true">
                                        <div class="row" style="padding: 5px 0px;">
                                            <div>
                                                <span class="col-md-4 control-label" style="padding: 0px;">
                                                    <asp:Label ID="lblModalReferralAddress" class="labelbold" runat="server" Style="margin-right: 100px;"
                                                        Text="Doctor Address:"></asp:Label>
                                                </span>
                                                <div class="col-md-6">
                                                    <div class="error-box">
                                                        <asp:TextBox ID="txtModalReferralAddress" Style="margin-left: -82px; width: 480px; height:43px;"
                                                            runat="server" placeholder="Enter Clinic or Hospital Address with Area or location and City"
                                                            class="form-control QRtextWidth" onkeyup="CheckCharacterMedicine(this,150);"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row" style="padding: 5px 0px;">
                                            <div>
                                                <span class="col-md-4 control-label" style="padding: 0px;">
                                                 <span class="astrick-sign" aria-hidden="true">*</span>
                                                    <asp:Label ID="lblModalReferralNumber" class="labelbold " runat="server"
                                                        Style="margin-right: 100px;" Text="Doctor Number:"></asp:Label>
                                                </span>
                                                <div class="col-md-6">
                                                    <div class="error-box">
                                                        <asp:TextBox ID="txtModalReferralNumber" Style="margin-left: -82px; width: 480px; height:43px;"
                                                            runat="server" placeholder="Enter 10 digit mobile number / Contact no of the Doctor"
                                                            MaxLength="10" class="form-control QRtextWidth" onkeypress="return isNumberKeyyy(event);"></asp:TextBox>
                                                        <%--<asp:RequiredFieldValidator runat="server" Enabled="false" ID="Required_Mob_No" 
                                                controltovalidate="txtModalReferralNumber" errormessage="Please Enter Another Contact Number !!!" />--%>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row" style="padding: 5px 0px;">
                                            <div>
                                                <span class="col-md-4 control-label" style="padding: 0px;">
                                                    <asp:Label ID="lblModalReferralEmail" class="labelbold" runat="server" Style="margin-right: 100px;"
                                                        Text="Doctor Email:"></asp:Label></span>
                                                <div class="col-md-6">
                                                    <div class="error-box">
                                                        <asp:TextBox runat="server" ID="hdnTxtConfirm" Visible="false"></asp:TextBox>
                                                        <asp:TextBox ID="txtModalReferralEmail" Style="margin-left: -82px; width: 480px; height:43px;"
                                                            runat="server" placeholder="Enter email id – preferably the facebook or gmail id"
                                                            class="form-control QRtextWidth" onkeypress="ResetErrorMessage();"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row" style="padding: 5px 0px;">
                                            <div>
                                                <span class="col-md-4 control-label" style="padding: 0px;">
                                                    <asp:Label ID="Label68" class="labelbold" runat="server" Style="margin-right: 100px;"
                                                        Text="Remark:"></asp:Label></span>
                                                <div class="col-md-6">
                                                    <div class="error-box">
                                                        <asp:TextBox ID="txtremark" Style="margin-left: -82px; width: 480px; height:43px;" runat="server"
                                                            placeholder="Accept Remark field" class="form-control QRtextWidth" onkeypress="ResetErrorMessage();"></asp:TextBox>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                           
                            </div>
                     
                            <br />
                       
                            <div class="row">
                                <div class="col-md-12">
                                    <center style="margin-bottom: 20px;">
                                        <asp:Button ID="btnModalAddDoctor" class="btn btn-primary" runat="server" Text="Submit"
                                            OnClick="btnModalAddDoctor_Click"  OnClientClick="return validMobileNumber()"/>&nbsp;&nbsp;&nbsp;&nbsp;
                                        <asp:Button ID="HdnBtnSubmit" class="btn btn-primary" runat="server" Style="display: none"
                                            Text="Submit" />
                                        <asp:Button ID="Button7" class="btn btn-primary" runat="server" Style="display: none"
                                            Text="Submit" OnClick="Hdnbtndata_Click" />
                                        <asp:Button ID="Button9" OnClick="btnImg_CloseFamilyFolder_Click1" CssClass="btn btn-primary"
                                            runat="server" Text="Close" />
                                    </center>
                                </div>
                            </div>
                           
                            <br />
                        </div>
                    </div>
                                      </ContentTemplate>
                            </asp:UpdatePanel>
                </div>
            </div>
        </asp:Panel>
        <%--  --------------------------------------------------------------------------------%>
        <!-- 16 may vinay -->
        <asp:UpdatePanel ID="UpdatePanel16" runat="server">
            <ContentTemplate>
                <cc6:ModalPopupExtender ID="ViewPreviousLabReports" BehaviorID="pvmp" runat="server"
                    PopupControlID="Panel6" TargetControlID="Button4" CancelControlID="ImageButtonCancel"
                    BackgroundCssClass="Background">
                </cc6:ModalPopupExtender>
                <asp:Button ID="Button4" runat="server" Text="" Style="visibility: hidden" />
                <asp:Panel ID="Panel6" runat="server" CssClass="Popup" align="center" Style="display: none;">
                    <div class="modal-dialog viewlabpopup">
                        <div class="modal-content compounder-modal-dialog-adjs">
                            <div class="modal-header">
                                <asp:UpdatePanel ID="UpdatePanel17" runat="server">
                                    <ContentTemplate>
                                        <asp:ImageButton ID="ImageButtonCancel" runat="server" ImageUrl="../images/close_btn_blue.png"
                                            OnClick="btnImg_CloseViewpreviouslabresults_Click" CssClass="imgbtn" />
                                    </ContentTemplate>
                                </asp:UpdatePanel>
                            </div>
                            <div class="table-responsive">
                                <div class="modal-body maxheight">
                                    <div class="row">
                                        <div class="col-md-12 printreceiptmargin">
                                            <div class="row">
                                                <div class="col-md-9 textalign">
                                                    <b>
                                                        <asp:Label ID="lblViewPreReport_Name" runat="server" CssClass="labelname1 paddingleft"></asp:Label>
                                                        <asp:Label ID="Label131" runat="server" CssClass="labelname1" Text="/"></asp:Label>
                                                        <asp:Label ID="lblViewPreReport_Gender" runat="server" CssClass="labelname1"></asp:Label>
                                                        <asp:Label ID="Label134" runat="server" CssClass="labelname1" Text="/"></asp:Label>
                                                        <asp:Label ID="lblViewPreReport_Age" runat="server" CssClass="labelname1"></asp:Label>
                                                        <asp:Label ID="Label136" runat="server" CssClass="labelname1" Text="Y"></asp:Label>
                                                    </b>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-12">
                                            <div class="row">
                                                <div class="col-md-3">
                                                    <asp:Label ID="Label35" runat="server" CssClass="labelbold pull-left" Text="Lab Doctor Name:"></asp:Label>
                                                </div>
                                                <div class="col-md-6">
                                                    <asp:Label ID="lblgetdoctorPV" runat="server" CssClass="lbl-black pull-left"></asp:Label>
                                                </div>
                                            </div>
                                        </div>
                                        <br />
                                        <div class="col-md-12">
                                            <div class="row">
                                                <div class="col-md-3">
                                                    <asp:Label ID="Label36" runat="server" CssClass="labelbold pull-left" Text="Lab Name:"></asp:Label>
                                                </div>
                                                <div class="col-md-6">
                                                    <asp:Label ID="lblgetlabPV" runat="server" CssClass="lbl-black pull-left"></asp:Label>
                                                </div>
                                            </div>
                                        </div>
                                        <br />
                                        <div class="col-md-12">
                                            <div class="row">
                                                <div class="col-md-3">
                                                    <asp:Label ID="Label37" runat="server" CssClass="labelbold pull-left" Text="Report Date:"></asp:Label>
                                                </div>
                                                <div class="col-md-6">
                                                    <asp:Label ID="lblgetReportdatePV" runat="server" CssClass="lbl-black pull-left"></asp:Label>
                                                </div>
                                            </div>
                                        </div>
                                        <br />
                                        <div class="col-md-12">
                                            <div class="row">
                                                <div class="col-md-3">
                                                    <asp:Label ID="Label103" runat="server" CssClass="labelbold pull-left" Text="Duration / Comment:"></asp:Label>
                                                </div>
                                                <div class="col-md-6 textalign">
                                                    <asp:Label ID="lbl_LabCommentPV" runat="server" CssClass="lbl-black pull-left"></asp:Label>
                                                </div>
                                            </div>
                                        </div>
                                        <br />
                                    </div>
                                    <br />
                                    <asp:GridView ID="grdPreviousVisitsLabResults" runat="server" AutoGenerateColumns="false"
                                        Width="100%" Height="70px">
                                        <Columns>
                                            <asp:TemplateField HeaderText="Sr." HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                <ItemTemplate>
                                                    <asp:Label ID="lblsrno" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                </ItemTemplate>
                                                <ItemStyle Width="3%" />
                                            </asp:TemplateField>
                                            <asp:TemplateField HeaderText="Lab Test Name" ItemStyle-Width="20%" ItemStyle-VerticalAlign="Middle"
                                                HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                <ItemTemplate>
                                                    <asp:Label ID="lblPrescriptionMedicinesPV" runat="server" CssClass="label1" Text='<%# Eval("Lab_Test_Description")%>'></asp:Label>
                                                </ItemTemplate>
                                            </asp:TemplateField>
                                            <asp:TemplateField HeaderText="Parameter Name" ItemStyle-Width="20%" ItemStyle-VerticalAlign="Middle"
                                                HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                <ItemTemplate>
                                                    <asp:Label ID="lblPrescriptionMorningPV" runat="server" CssClass="label1" Text='<%# Eval("Parameter_Name")%>'></asp:Label>
                                                </ItemTemplate>
                                            </asp:TemplateField>
                                            <asp:TemplateField HeaderText="Parameter Value" ItemStyle-Width="10%" ItemStyle-VerticalAlign="Middle"
                                                HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1 wordbreakgrid">
                                                <ItemTemplate>
                                                    <asp:Label ID="lblParameterValuePV" runat="server" CssClass="label1" Text='<%# Eval("Test_Parameter_Value")%>'></asp:Label>
                                                </ItemTemplate>
                                            </asp:TemplateField>
                                        </Columns>
                                        <EmptyDataTemplate>
                                            <center>
                                                <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float" runat="server">---</asp:Label></center>
                                        </EmptyDataTemplate>
                                        <AlternatingRowStyle BackColor="White" />
                                        <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                        <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                        <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                        <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                    </asp:GridView>
                                    <br />
                                    <br />
                                    <div class="row">
                                        <div class="col-md-12">
                                            <asp:Button ID="btnclosePreLabresults" CssClass="btn btn-primary" runat="server"
                                                Text="Close" OnClick="btnImg_CloseViewpreviouslabresults_Click" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <asp:Button ID="Button5" runat="server" Text="Close" Style="visibility: hidden" />
                </asp:Panel>
            </ContentTemplate>
        </asp:UpdatePanel>
        <asp:UpdatePanel ID="UpdatePanel47" runat="server">
            <ContentTemplate>
                <cc8:ModalPopupExtender ID="Billing_details_PopUp" BehaviorID="ABBD" runat="server"
                    PopupControlID="Panel13" TargetControlID="Button17" BackgroundCssClass="Background">
                </cc8:ModalPopupExtender>
                <asp:Button ID="Button17" runat="server" Text="" Style="visibility: hidden" />
                <asp:Panel ID="Panel13" runat="server" CssClass="Popup billing_popUp" align="center"
                    Style="display: none; left: 220.5px !important">
                    <div class="modal-dialog" style="width: 120%;">
                        <div class="modal-content compounder-modal-dialog-adjs maxheight" style="width: 129%;
                            height: 601px;">
                            <div class="modal-header">
                                <asp:UpdatePanel ID="UpdatePanel48" runat="server">
                                    <ContentTemplate>
                                        <asp:ImageButton ID="ImageButton6" runat="server" ImageUrl="../images/close_btn_blue.png"
                                            OnClick="ImageButton6_Onclick" CssClass="imgbtn" />
                                    </ContentTemplate>
                                </asp:UpdatePanel>
                            </div>
                            <div class="table-responsive">
                                <div class="modal-body col-md-offset-1">
                                    <div class="row">
                                        <div class="col-md-8 textalign paddingleft">
                                            <b>
                                                <asp:Label ID="lblBilling_PatientName" CssClass="labelname1 PPlabelname" runat="server"
                                                    Text=""></asp:Label>
                                                <asp:Label ID="Label98" CssClass="labelname1" runat="server" Text="/"></asp:Label>
                                                <asp:Label ID="lblBilling_Gender" CssClass="labelname1" runat="server" Text=""></asp:Label>
                                                <asp:Label ID="Label100" CssClass="labelname1" runat="server" Text="/"></asp:Label>
                                                <asp:Label ID="lblBilling_Age" CssClass="labelname1" runat="server" Text=""></asp:Label>
                                                <asp:Label ID="Label102" CssClass="labelname1" runat="server" Text="Y"></asp:Label>
                                            </b>
                                        </div>
                                        <div class="col-md-4 textalign paddingleft paddingright">
                                            <div class="col-md-8 paddingleft">
                                                <asp:Label ID="Label38" CssClass="labelbold" runat="server" Text="Total Billed Amount(Rs):"></asp:Label>
                                            </div>
                                            <div class="col-md-4 paddingleft">
                                                <asp:TextBox ID="txtTotalFees" runat="server" class="form-control" ReadOnly="true"
                                                    BorderStyle="None" Style="text-align: right;"></asp:TextBox>
                                            </div>
                                        </div>
                                    </div>
                                    <br />
                                    <div class="row">
                                        <div class="col-md-12 textalign paddingleft">
                                            <div class="table-responsive">
                                                <asp:UpdatePanel ID="UpdatePanel49" runat="server">
                                                    <ContentTemplate>
                                                        <asp:GridView ID="grd_Detailed_Billing" runat="server" AutoGenerateColumns="false"
                                                            Width="100%" OnRowDataBound="grd_BillingDetail_RowDataBound" Height="70px">
                                                            <Columns>
                                                                <asp:TemplateField HeaderText="Sr." HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1">
                                                                    <ItemTemplate>
                                                                        <asp:Label ID="lblsrno" CssClass="label1" runat="server" Text='<%# Container.DataItemIndex + 1 %>'></asp:Label>
                                                                        <asp:HiddenField ID="hdn_IsDefault" runat="server" Value='<%#Eval("Isdefault")%>' />
                                                                        <asp:HiddenField ID="hdn_Visittype" runat="server" Value='<%#Eval("Visit_Type")%>' />
                                                                    </ItemTemplate>
                                                                    <ItemStyle Width="3%" />
                                                                </asp:TemplateField>
                                                                <asp:TemplateField HeaderText="Group" ItemStyle-Width="450px" ItemStyle-VerticalAlign="Middle"
                                                                    SortExpression="Cat_Short_Name" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                    <ItemTemplate>
                                                                        <asp:Label ID="lblGroupName" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Billing_Group_Name") %>'></asp:Label>
                                                                    </ItemTemplate>
                                                                    <ItemStyle Width="450px"></ItemStyle>
                                                                </asp:TemplateField>
                                                                <asp:TemplateField HeaderText="Sub-Group" ItemStyle-Width="450px" ItemStyle-VerticalAlign="Middle"
                                                                    SortExpression="CatSub_Description" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                    <ItemTemplate>
                                                                        <asp:Label ID="lblSubGroup" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Billing_Subgroup_Name") %>'></asp:Label>
                                                                        <asp:Label ID="lbl_hdn_subgroup" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Billing_Subgroup_Name") %>'
                                                                            Visible="false"></asp:Label>
                                                                    </ItemTemplate>
                                                                    <ItemStyle Width="450px"></ItemStyle>
                                                                </asp:TemplateField>
                                                                <asp:TemplateField HeaderText="Details" ItemStyle-Width="300px" ItemStyle-VerticalAlign="Middle"
                                                                    SortExpression="Brand_Name" HeaderStyle-CssClass="cls_left_aligned" ControlStyle-CssClass="cls_left_float label1">
                                                                    <ItemTemplate>
                                                                        <asp:Label ID="lblDetail" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Billing_Details") %>'></asp:Label>
                                                                        <asp:Label ID="lbl_hdnDetail" CssClass="label1" runat="server" Text='<%#DataBinder.Eval(Container.DataItem, "Billing_Details") %>'
                                                                            Visible="false"></asp:Label>
                                                                    </ItemTemplate>
                                                                    <ItemStyle Width="300px"></ItemStyle>
                                                                </asp:TemplateField>
                                                                <asp:TemplateField HeaderText="Select" ItemStyle-Width="100px">
                                                                    <ItemTemplate>
                                                                        <asp:CheckBox ID="radiobtn_Billing" runat="server" GroupName="radiogroup" AutoPostBack="true"
                                                                            OnCheckedChanged="radiobtn_Billing_CheckedChanged" />
                                                                        <asp:HiddenField ID="hdn_rdnbtn_value" runat="server" Value='<%#Eval("Billing_ID")%>' />
                                                                        <asp:HiddenField ID="hdn_Default_Fees" runat="server" Value='<%#Eval("Default_Fees")%>' />
                                                                    </ItemTemplate>
                                                                </asp:TemplateField>
                                                                <asp:TemplateField HeaderText="Total Fees(Rs)" ItemStyle-Width="200px" ItemStyle-VerticalAlign="Middle"
                                                                    SortExpression="Brand_Name" HeaderStyle-CssClass="cls_right_aligned" ControlStyle-CssClass="cls_right_float label1 fntsz">
                                                                    <ItemTemplate>
                                                                        <asp:TextBox ID="txtFees" runat="server" class="form-control" Width="100%" onkeypress="return isNumbereyBilledAmount(event)"
                                                                            OnTextChanged="txtFees_TextChanged" AutoPostBack="true" BorderStyle="None"></asp:TextBox>
                                                                    </ItemTemplate>
                                                                    <ItemStyle Width="200px"></ItemStyle>
                                                                </asp:TemplateField>
                                                            </Columns>
                                                            <EmptyDataTemplate>
                                                                <center>
                                                                    <asp:Label ID="lblNoRecordFound" CssClass="label1 cls_left_float fntsz" runat="server">---</asp:Label></center>
                                                            </EmptyDataTemplate>
                                                            <AlternatingRowStyle BackColor="White" />
                                                            <FooterStyle BackColor="#507CD1" Font-Bold="True" ForeColor="White" />
                                                            <HeaderStyle BackColor="#507CD1" ForeColor="White" Font-Names="" />
                                                            <RowStyle BackColor="#EFF3FB" Font-Names="" HorizontalAlign="Center" />
                                                            <SelectedRowStyle BackColor="#D1DDF1" Font-Bold="True" ForeColor="#333333" />
                                                        </asp:GridView>
                                                    </ContentTemplate>
                                                </asp:UpdatePanel>
                                            </div>
                                        </div>
                                    </div>
                                    <br />
                                    <div class="row">
                                        <div class="col-md-12">
                                            <div class="row">
                                                <asp:Label ID="lblError_Billing_Details" Text="" class="error-box" runat="server"></asp:Label>
                                            </div>
                                        </div>
                                    </div>
                                    <br />
                                    <div class="row">
                                        <div class="col-md-12">
                                            <asp:Button ID="btnBilling_Detail_Submit" runat="server" Text="Submit" CssClass="btn btn-primary">
                                            </asp:Button>
                                            <!-- OnClick="btnBilling_Detail_Submit_Click" -->
                                            <asp:Button ID="btnBilling_Detail_Cancel" CssClass="btn btn-primary" runat="server"
                                                Text="Cancel" OnClick="btnBilling_Detail_Cancel_Click"></asp:Button>
                                            <asp:Button ID="btn_Billing_Close" CssClass="btn btn-primary" runat="server" Text="Close"
                                                OnClick="ImageButton6_Onclick" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <asp:Button ID="Button19" runat="server" Text="Close" Style="visibility: hidden" />
                </asp:Panel>
            </ContentTemplate>
        </asp:UpdatePanel>
        <!-- end updt -->
    </div>
</asp:Content>
