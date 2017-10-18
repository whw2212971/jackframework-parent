(function (global) {

    var $ = global.jQuery,
        $win = $(global),
        $doc = $(global.document);

    function jackControlsInit(rootElement) {
        var $root = $(rootElement);

        $root.find('.form-select>select').each(function () {
            var $self = $(this);
            var $parent = $self.parent();
            var placeholder = $parent.attr('data-placeholder') || $self.prop('title') || '';
            $self.on('change', function () {
                var $option = $self.find('option:selected');
                if ($option.length === 0 || $self.val() === '') {
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