(function (global) {

    var $ = global.jQuery,
        $win = $(global),
        $doc = $(global.document);

    function jackControlsInit(rootElement) {
        var $root = $(rootElement);

        // Form Select Control
        $root.find('.form-select>select').each(function () {
            var $select = $(this);
            var $parent = $select.parent();
            var placeholder = $parent.attr('data-placeholder') || $select.prop('title') || '';
            $select.on('change', function () {
                var $option = $select.find('option:selected');
                if ($option.length === 0 || $select.val() === '') {
                    $parent.attr('data-placeholder', placeholder).removeClass('selected');
                } else {
                    $parent.attr('data-placeholder', $option.text()).addClass('selected');
                }
            }).on('focus', function () {
                $parent.addClass('focus');
            }).on('blur', function () {
                $parent.removeClass('focus');
            }).trigger('change');
        });

    }

    $(function () {
        jackControlsInit($doc);
    });

    $.jackControlsInit = jackControlsInit;

})(window);