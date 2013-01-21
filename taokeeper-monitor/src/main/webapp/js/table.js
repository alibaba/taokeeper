var Ptr = document.getElementById("tab").getElementsByTagName("tr");
function $() {
	for ( var i = 1; i < Ptr.length + 1; i++) {
		Ptr[i - 1].className = (i % 2 > 0) ? "t1" : "t2";
	}
}
window.onload = $;
for ( var i = 0; i < Ptr.length; i++) {
	Ptr[i].onmouseover = function() {
		this.tmpClass = this.className;
		this.className = "t3";
	};
	Ptr[i].onmouseout = function() {
		this.className = this.tmpClass;
	};
}