//1.计算总价和小计
function productCount() {
    var $tr = $('#shopping').find('tr[id]');
    //总价格
    var summer = 0;
    //总积分
    var integral = 0;
    $tr.each(function () {
        var num = $(this).children('.cart_td_6').find('input').val();
        var price = num*$(this).children('.cart_td_5').text();
        $(this).children('.cart_td_7').text(price);
        summer += price;
        integral += num*$(this).children('.cart_td_4').text();
    });
    $('#total').text(summer);
    $('#integral').text(integral);
}

$(function(){

    $('.hand').click(function () {//数量加减
        if($(this).attr('alt')=='add'){
            var num = parseInt($(this).prev().val())+1
            $(this).prev().val(num);
        }else{
            var num = parseInt($(this).next().val())-1
            num = num < 1 ? 1:num;
            $(this).next().val(num);
        }
        productCount();
    });

    $('.cart_td_8 a').click(function () {//删除单个
        $(this).parent().parent().prev().remove();
        $(this).parent().parent().remove();
        productCount();
    });
    
    $('#deleteAll').click(function () {//删除所选
        $('.cart_td_1 input:checked').parent().parent().prev().remove();
        $('.cart_td_1 input:checked').parent().parent().remove();
        productCount();
    });

    $('.cart_td_1 input').change(function () {//小复选框点击事件
        var sizeAll = $('.cart_td_1 input').size();
        var selectSize = $('.cart_td_1 input:checked').size()
        if(selectSize < sizeAll){
            $('#allCheckBox').prop('checked',false);
        }else{
            $('#allCheckBox').prop('checked',true);
        }
    });

    //全选全不选
    $('#allCheckBox').click(function () {
        $('.cart_td_1 input').prop('checked',$(this).prop('checked'));
    });

    productCount();

});


