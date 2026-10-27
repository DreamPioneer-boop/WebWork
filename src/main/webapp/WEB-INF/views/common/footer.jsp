<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    </main>
  </div>
</div>

<script>
  // 侧边栏折叠
  document.getElementById('collapseBtn').addEventListener('click', function () {
    document.getElementById('sidebar').classList.toggle('collapsed');
  });
  // 头像下拉菜单
  var userInfo = document.getElementById('userInfo');
  var drop = document.getElementById('dropdownMenu');
  userInfo.addEventListener('click', function (e) {
    e.stopPropagation();
    drop.classList.toggle('show');
  });
  document.addEventListener('click', function () {
    drop.classList.remove('show');
  });
  // 删除类操作二次确认
  document.querySelectorAll('form[data-confirm]').forEach(function (f) {
    f.addEventListener('submit', function (e) {
      if (!confirm(f.getAttribute('data-confirm'))) e.preventDefault();
    });
  });
</script>
</body>
</html>
