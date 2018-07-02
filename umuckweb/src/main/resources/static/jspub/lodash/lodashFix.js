// 修改loadsh的加减乘除方法,讲参数都转成数值再计算
// 加法
_.add = createMathOperation(function(augend, addend) {
			return augend + addend;
		}, 0);
// 减法
_.subtract = createMathOperation(function(minuend, subtrahend) {
			return minuend - subtrahend;
		}, 0);
// 乘法
_.multiply = createMathOperation(function(multiplier, multiplicand) {
			return multiplier * multiplicand;
		}, 1);
// 除法
_.divide = createMathOperation(function(dividend, divisor) {
			return dividend / divisor;
		}, 1);

function createMathOperation(operator, defaultValue) {
	return function(value, other) {
		var result;
		if (value === undefined && other === undefined) {
			return defaultValue;
		}
		if (value !== undefined) {
			result = value;
		}
		if (other !== undefined) {
			if (result === undefined) {
				return other;
			}
			value = _.toNumber(value);
			other = _.toNumber(other);
			result = operator(value, other);
		}
		return result;
	};
}